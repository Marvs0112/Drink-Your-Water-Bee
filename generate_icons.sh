#!/bin/bash

# Create necessary directories
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi

# Convert and resize the icon for different densities
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png

# Create round icons
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher_round.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher_round.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

# Create foreground icons
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher_foreground.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher_foreground.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher_foreground.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher_foreground.png
magick app/src/main/res/drawable/drinkyourwaterbee.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png 