#!/bin/bash
# Generate PNG diagrams from PlantUML server using pre-rendered URLs

cd /home/kassimi/Documents/work/eBank/figures

echo "Downloading sequence diagrams..."
# These are the URLs that PlantUML generated from the browser - using similar encoding
# For now, let's use a simpler approach with curl and plantuml server

cd /home/kassimi/Documents/work/eBank/temp_gen

# Install plantuml if needed (using jar)
echo "Generating diagrams using plantuml command line..."

# Alternative: use online API with curl
for file in *.puml; do
    if [ -f "$file" ]; then
        basename="${file%.puml}"
        echo "Processing $file -> $basename.png"
        curl -X POST --data-binary "@$file" https://www.plantuml.com/plantuml/png/ -o "../figures/$basename.png"
    fi
done

echo "Done!"
