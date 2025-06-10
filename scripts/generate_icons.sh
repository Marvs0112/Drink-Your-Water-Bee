#!/bin/bash

# Create necessary directories
mkdir -p app/src/main/res/mipmap-mdpi
mkdir -p app/src/main/res/mipmap-hdpi
mkdir -p app/src/main/res/mipmap-xhdpi
mkdir -p app/src/main/res/mipmap-xxhdpi
mkdir -p app/src/main/res/mipmap-xxxhdpi

# Save the bee icon to app_icon.png
cat > app_icon.png << 'EOL'
<svg width="192" height="192" xmlns="http://www.w3.org/2000/svg">
  <!-- Water Drop -->
  <path d="M96 20 L150 120 A54 54 0 1 1 42 120 Z" fill="#4FC3F7"/>
  <!-- Bee Wings -->
  <path d="M70 60 Q40 40 60 20 Q80 40 70 60" fill="none" stroke="black" stroke-width="4"/>
  <path d="M122 60 Q152 40 132 20 Q112 40 122 60" fill="none" stroke="black" stroke-width="4"/>
  <!-- Bee Body -->
  <circle cx="96" cy="60" r="20" fill="black"/>
  <!-- Bee Stripes -->
  <rect x="86" y="55" width="20" height="5" fill="#FDD835"/>
  <rect x="86" y="65" width="20" height="5" fill="#FDD835"/>
  <!-- Antennae -->
  <path d="M86 45 Q76 35 80 25" fill="none" stroke="black" stroke-width="2"/>
  <path d="M106 45 Q116 35 112 25" fill="none" stroke="black" stroke-width="2"/>
  <!-- Honey Drips -->
  <path d="M80 140 Q85 160 90 140" fill="#FFA000"/>
  <path d="M95 150 Q100 170 105 150" fill="#FFA000"/>
  <path d="M110 140 Q115 160 120 140" fill="#FFA000"/>
</svg>
EOL

# Convert SVG to PNG
magick convert app_icon.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher.png
magick convert app_icon.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher.png
magick convert app_icon.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher.png
magick convert app_icon.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher.png
magick convert app_icon.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png

# Create round icons
magick convert app_icon.png -resize 48x48 -alpha set -background none \( +clone -alpha extract -draw 'fill black circle 24,24 24,0' -alpha extract -morphology Distance Euclidean:1 -level 50,100% \) -compose Multiply -composite app/src/main/res/mipmap-mdpi/ic_launcher_round.png
magick convert app_icon.png -resize 72x72 -alpha set -background none \( +clone -alpha extract -draw 'fill black circle 36,36 36,0' -alpha extract -morphology Distance Euclidean:1 -level 50,100% \) -compose Multiply -composite app/src/main/res/mipmap-hdpi/ic_launcher_round.png
magick convert app_icon.png -resize 96x96 -alpha set -background none \( +clone -alpha extract -draw 'fill black circle 48,48 48,0' -alpha extract -morphology Distance Euclidean:1 -level 50,100% \) -compose Multiply -composite app/src/main/res/mipmap-xhdpi/ic_launcher_round.png
magick convert app_icon.png -resize 144x144 -alpha set -background none \( +clone -alpha extract -draw 'fill black circle 72,72 72,0' -alpha extract -morphology Distance Euclidean:1 -level 50,100% \) -compose Multiply -composite app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png
magick convert app_icon.png -resize 192x192 -alpha set -background none \( +clone -alpha extract -draw 'fill black circle 96,96 96,0' -alpha extract -morphology Distance Euclidean:1 -level 50,100% \) -compose Multiply -composite app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

# Create foreground icons
magick convert app_icon.png -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher_foreground.png
magick convert app_icon.png -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher_foreground.png
magick convert app_icon.png -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher_foreground.png
magick convert app_icon.png -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher_foreground.png
magick convert app_icon.png -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher_foreground.png

# Clean up
rm app_icon.png 