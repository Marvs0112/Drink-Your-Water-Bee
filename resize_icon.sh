#!/bin/bash

# Create directories if they don't exist
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi

# Regular icons
magick drinkyourwaterbee.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher.png
magick drinkyourwaterbee.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher.png
magick drinkyourwaterbee.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher.png
magick drinkyourwaterbee.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher.png
magick drinkyourwaterbee.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png

# Round icons (same image, will be masked by Android)
cp app/src/main/res/mipmap-mdpi/ic_launcher.png app/src/main/res/mipmap-mdpi/ic_launcher_round.png
cp app/src/main/res/mipmap-hdpi/ic_launcher.png app/src/main/res/mipmap-hdpi/ic_launcher_round.png
cp app/src/main/res/mipmap-xhdpi/ic_launcher.png app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
cp app/src/main/res/mipmap-xxhdpi/ic_launcher.png app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
cp app/src/main/res/mipmap-xxxhdpi/ic_launcher.png app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png 