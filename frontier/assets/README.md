All images will be packed  in to one texture per subfolder.

They should be grouped by level, so that they can be loaded in one turn and not during gameplay.


> [!CAUTION]
>
> When adding new texures you need to repack them with `./gradlew texturepacker`


## Troubleshooting

If the gradle texturepacker fails add `--stacktrace` to get more information.

If the set size is to small change the settings in `pack.json`


For more information consult: https://libgdx.com/wiki/tools/texture-packer
