# SkinComposer Documentation

## What is SkinComposer?
SkinComposer is a specialized tool for creating and managing skins for LibGDX. It simplifies the process of defining UI elements, styles, and assets without manually editing JSON files.

## Installation
You can download SkinComposer from the following GitHub repository: [Download](https://github.com/raeleus/skin-composer/releases).

## Understanding Skins in LibGDX
A SkinComposer export consists of the following files:
- JSON Skin => The skin as a JSON
- PNG Skin => An image containing all images from the skin
- Atlas Skin => File containing the coordinates of each image in the PNG
- Font files => Font files

## Working with Drawables
By clicking on Project => Drawables you can open a menu where you can add image files:
![SkinComposer Project Menu](https://github.com/user-attachments/assets/ada8f349-0006-4ac6-be1c-9e5d6d827d7f)
![SkinComposer Image Upload](https://github.com/user-attachments/assets/7e491479-2cec-42c3-be27-b29f00935faf).

## Fonts in SkinComposer
By clicking on Project => Fonts you can open a menu where you can add font files. You may have to use programs like FontForge to convert them into the correct file extension:
![SkinComposer Font Upload](https://github.com/user-attachments/assets/b1c332ab-2952-45aa-80b7-9046b8418c62)

## Managing Styles
### What are styles?
A style is a collection of properties that define the appearance of a UI widget. You can for example define a medieval style and a SciFi style or perhaps a style for the Start Menu and one for the Pause Menu, etc. A style can consists of different fonts, colours, images, etc. Each UI widget in LibGDX requires a style to define how it looks.
### Creating and modifying styles
If you want to create a new style you have to select the class (for example Button) and then press on the "+" sign next to the style list. Afterwards you can start customising the style as you'd like:
![SkinComposer Style Selector](https://github.com/user-attachments/assets/0f8e6250-435b-4cb1-8a8f-5375411b4c87)
### Style Inheritance
Style inheritance allows a new style to reuse properties from an existing style while overriding specific attributes. This reduces duplication and makes skin management more efficient. 
#### How to Use Style Inheritance
To use style inheritance you have to select a parent style. Your style will inherit all of the parent's properties. Afterwards you can do customisation for the selected style.
![SkinComposer Style Inheritance](https://github.com/user-attachments/assets/09c97d6a-0cbb-430c-a77e-5f7227ea2273)

## Editing Skin JSON Files
If you want to edit an existing skin you have to first import said skin and afterwards you can perform changes (for example a different font) to specific classes (for example buttons and lists).

## Exporting & Importing Skins in LibGDX
### Exporting skin files
To export a skin click on File => Export and then select where you want to store the skin. Make sure that the correct ticks are set (as seen in the image below):

![SkinComposer Export](https://github.com/user-attachments/assets/39b1884c-9808-4d88-84fd-56ae13d4584e)
![SkinComposer Export](https://github.com/user-attachments/assets/6f27de00-7e52-4ea1-baf2-f6996f8a7c91)

You can also click on File => Save As to save the actual project (as an SCMP file) if you want to modify the skin in the future.

### Importing skin files
Just like for an export you click on File but then choose "Open" to select the SkinComposer project you want to open.

### Loading Skins in Code
```java
Skin skin = assetManager.get("skin/skin.json", Skin.class);

// Create a TextButton and apply the style "myStyle" from the skin
TextButton button = new TextButton("Click Me", skin, "myStyle");

// Add the button to the stage
stage.addActor(button);
```
