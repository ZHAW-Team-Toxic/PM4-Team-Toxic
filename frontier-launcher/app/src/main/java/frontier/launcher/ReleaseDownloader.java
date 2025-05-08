package frontier.launcher;

import java.io.*;
import java.net.URI;
import java.net.http.*;
import java.nio.file.*;
import java.util.zip.*;
import org.json.*;

/**
 * ReleaseDownloader is a self-updating launcher for platform-specific builds
 * of an application hosted on GitHub. It checks the latest release from a
 * configured repository, downloads the matching platform ZIP file (if necessary),
 * extracts it, and launches the app.
 *
 * Key features:
 * - Detects platform (e.g., winX64, linuxX64, macM1, macX64)
 * - Tracks latest downloaded version in `.version` (tag + published_at)
 * - Only re-downloads if tag or publish date changes (in case of re-published releases)
 * - Cleans up old ZIPs and folders to save space
 * - Supports launching `.jar` (non-mac) or `.app` (macOS) builds
 *
 * Decisions:
 * - Tag+published_at check: ensures even re-tagged releases get picked up
 * - Cleanup step: saves disk space and avoids clutter
 * - Extract ZIP every time unless already extracted
 */

public class ReleaseDownloader {
    private static final String REPO = "ZHAW-Team-Toxic/PM4-Team-Toxic";
    private static final String DOWNLOAD_DIR = "releases";
    private static final Path VERSION_FILE = Paths.get(DOWNLOAD_DIR, ".version");

    public static void main(String[] args) throws Exception {
        String platformTag = detectPlatform();
        System.out.println("Detected platform: " + platformTag);

        // 1. Get latest release info
        JSONObject releaseJson = findBestMatchingRelease(platformTag);
        String latestTag = releaseJson.optString("tag_name", "latest");
        String publishedAt = releaseJson.optString("published_at", "");

        // 2. Check if current version is up-to-date
        boolean isUpToDate = false;
        if (Files.exists(VERSION_FILE)) {
            String jsonText = Files.readString(VERSION_FILE);
            JSONObject currentVersion = new JSONObject(jsonText);
            String currentTag = currentVersion.optString("tag", "");
            String currentPublishedAt = currentVersion.optString("published_at", "");

            isUpToDate = currentTag.equals(latestTag) && currentPublishedAt.equals(publishedAt);
        }

        if (isUpToDate) {
            System.out.println("Already up to date: " + latestTag);
            Path extractDir = Paths.get(DOWNLOAD_DIR, latestTag);
            Path rootSubfolder = findFirstSubdirectory(extractDir);
            if (rootSubfolder == null) throw new IOException("Cannot find extracted app.");
            launchApp(platformTag, rootSubfolder);
            System.out.println("Launcher done. Exiting.");
            System.exit(0);
        }

        System.out.println("New version detected: " + latestTag + " (" + publishedAt + ")");

        // 3. Find zip asset
        JSONArray assets = releaseJson.getJSONArray("assets");
        JSONObject zipAsset = null;
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.getJSONObject(i);
            String name = asset.getString("name").toLowerCase();
            if (name.contains(platformTag.toLowerCase()) && name.endsWith(".zip")) {
                zipAsset = asset;
                break;
            }
        }

        if (zipAsset == null) throw new IOException("No matching ZIP asset for: " + platformTag);

        String zipName = zipAsset.getString("name");
        Path localZip = Paths.get(DOWNLOAD_DIR, zipName);
        Path extractDir = Paths.get(DOWNLOAD_DIR, latestTag);

        Files.createDirectories(Paths.get(DOWNLOAD_DIR));
        cleanupOldVersions(latestTag);

        // 4. Download ZIP
        if (!Files.exists(localZip)) {
            System.out.println("Downloading " + zipName + "...");
            downloadFile(zipAsset.getString("browser_download_url"), localZip);
        }

        // 5. Extract
        if (!Files.exists(extractDir)) {
            System.out.println("Extracting...");
            unzip(localZip, extractDir);
            Files.deleteIfExists(localZip);
        }

        // 6. Store new version
        JSONObject versionInfo = new JSONObject();
        versionInfo.put("tag", latestTag);
        versionInfo.put("published_at", publishedAt);
        Files.writeString(VERSION_FILE, versionInfo.toString(2));

        // 7. Launch
        Path rootSubfolder = findFirstSubdirectory(extractDir);
        if (rootSubfolder == null) throw new IOException("Cannot find extracted app.");
        launchApp(platformTag, rootSubfolder);
        System.out.println("Launcher done. Exiting.");
        System.exit(0);
    }

    /**
     * Deletes all ZIPs and folders in the download dir that don't match the given tag.
     */
    private static void cleanupOldVersions(String keepTag) throws IOException {
        Path base = Paths.get(DOWNLOAD_DIR);
        if (!Files.exists(base)) return;

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(base)) {
            for (Path path : stream) {
                String name = path.getFileName().toString();
                if (name.equals(".version") || name.equals(keepTag)) continue;
                if (Files.isDirectory(path) || name.endsWith(".zip")) {
                    System.out.println("Deleting old version: " + path);
                    if (Files.isDirectory(path)) {
                        deleteDirectoryRecursively(path);
                    } else {
                        Files.deleteIfExists(path);
                    }
                }
            }
        }
    }
    

     /**
     * Downloads the file from GitHub to a local path.
     */
    private static void downloadFile(String downloadUrl, Path destination) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
            .followRedirects(HttpClient.Redirect.ALWAYS)
            .build();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(downloadUrl))
            .header("User-Agent", "Java ReleaseLauncher")
            .header("Accept", "application/octet-stream")
            .build();

        HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(destination));
        if (response.statusCode() != 200) {
            throw new IOException("Failed to download file. Status: " + response.statusCode());
        }
    }

    private static void deleteDirectoryRecursively(Path path) throws IOException {
        if (!Files.exists(path)) return;
        Files.walk(path)
             .sorted((a, b) -> b.compareTo(a))
             .forEach(p -> {
                 try {
                     Files.delete(p);
                 } catch (IOException e) {
                     System.err.println("Failed to delete: " + p);
                 }
             });
    }

    /**
     * Detects the current platform and architecture.
     */
    private static String detectPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();

        if (os.contains("win") && arch.contains("64")) return "winX64";
        if (os.contains("linux") && arch.contains("64")) return "linuxX64";
        if (os.contains("mac")) {
            if (arch.contains("aarch64") || arch.contains("arm")) return "macM1";
            else return "macX64";
        }

        throw new RuntimeException("Unsupported OS or architecture: " + os + " " + arch);
    }

    /**
     * Finds the latest release from GitHub matching the current platform.
     */
    private static JSONObject findBestMatchingRelease(String platformTag) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.github.com/repos/" + REPO + "/releases"))
            .header("User-Agent", "Java ReleaseLauncher")
            .header("Accept", "application/vnd.github+json")
            .build();
    
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to fetch releases. Status: " + response.statusCode());
        }
    
        JSONArray releases = new JSONArray(response.body());
        for (int i = 0; i < releases.length(); i++) {
            JSONObject release = releases.getJSONObject(i);
            JSONArray assets = release.optJSONArray("assets");
            if (assets == null || assets.isEmpty()) continue;
    
            for (int j = 0; j < assets.length(); j++) {
                JSONObject asset = assets.getJSONObject(j);
                String name = asset.optString("name", "").toLowerCase();
                if (name.contains("launcher")) continue; // 🔒 Prevent downloading the launcher
                if (name.startsWith("frontier-") && name.contains(platformTag.toLowerCase()) && name.endsWith(".zip")) {
                    System.out.println("Using release: " + release.optString("tag_name", "<no-tag>") + " (asset: " + name + ")");
                    return release;
                }
            }
        }
    
        throw new IOException("No release contained a matching 'frontier-PLATFORM.zip' for platform: " + platformTag);
    }

    /**
     * Extracts a ZIP file into a target directory.
     */
    private static void unzip(Path zipFilePath, Path extractTo) throws IOException {
        Files.createDirectories(extractTo);
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath.toFile()))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path outPath = extractTo.resolve(entry.getName()).normalize();
                if (!outPath.startsWith(extractTo)) {
                    throw new IOException("Zip entry is outside target dir: " + entry.getName());
                }

                if (entry.isDirectory()) {
                    Files.createDirectories(outPath);
                } else {
                    Files.createDirectories(outPath.getParent());
                    try (OutputStream out = Files.newOutputStream(outPath)) {
                        zis.transferTo(out);
                    }
                }
                zis.closeEntry();
            }
        }
    }

    /**
     * Finds the first subdirectory inside a given directory.
     */
    private static Path findFirstSubdirectory(Path parent) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(parent)) {
            for (Path path : stream) {
                if (Files.isDirectory(path)) {
                    return path;
                }
            }
        }
        return null;
    }

    private static Path findPathEndingWith(Path baseDir, String suffix) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseDir)) {
            for (Path path : stream) {
                if (path.getFileName().toString().endsWith(suffix)) {
                    return path;
                }
            }
        }
        return null;
    }

    /**
     * Launches the application depending on the platform.
     */
    private static void launchApp(String platformTag, Path rootSubfolder) throws IOException, InterruptedException {
        if (platformTag.startsWith("mac")) {
            Path appBundle = findPathEndingWith(rootSubfolder, ".app");
            if (appBundle != null) {
                Path exec = appBundle.resolve("Contents/MacOS/Frontier");
                if (Files.exists(exec)) {
                    System.out.println("Launching macOS app: " + exec);
                    Process process = new ProcessBuilder(exec.toString()).inheritIO().start();
                    process.waitFor();
                    return;
                }
            }
            throw new IOException("No .app or MacOS binary found.");
        } else {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(rootSubfolder)) {
                for (Path path : stream) {
                    if (path.toString().endsWith(".jar")) {
                        System.out.println("Launching JAR: " + path);
                        Process process = new ProcessBuilder("java", "-jar", path.toString()).inheritIO().start();
                        process.waitFor();
                        return;
                    }
                }
            }
            throw new IOException("No .jar found to launch.");
        }
    }
}