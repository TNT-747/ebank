#!/usr/bin/env python3
import base64
import zlib
import os
import urllib.request

def plantuml_encode(plantuml_text):
    """Encode PlantUML text to URL-safe format."""
    zlibbed_str = zlib.compress(plantuml_text.encode('utf-8'))
    compressed = zlibbed_str[2:-4]  # Remove zlib header and checksum
    return base64.b64encode(compressed).decode('ascii').translate(
        str.maketrans('+/', '-_')
    ).rstrip('=')

def generate_png_from_puml(puml_filepath, output_filepath):
    """Read PUML file, encode it, and download PNG from PlantUML server."""
    with open(puml_filepath, 'r') as f:
        plantuml_text = f.read()
    
    encoded = plantuml_encode(plantuml_text)
    url = f"https://www.plantuml.com/plantuml/png/{encoded}"
    
    print(f"Downloading from: {url[:80]}...")
    urllib.request.urlretrieve(url, output_filepath)
    print(f"Saved: {output_filepath}")

# Process all PUML files
puml_files = {
    'use_case_diagram.puml': 'use_case_diagram.png',
    'class_diagram.puml': 'class_diagram.png',
    'sequence_transfer.puml': 'sequence_transfer.png',
    'sequence_auth.puml': 'sequence_authentication.png',
    'sequence_create_account.puml': 'sequence_create_account.png',
    'architecture_layers.puml': 'architecture_layers.png',
    'architecture_overview.puml': 'architecture_overview.png',
    'component_diagram.puml': 'component_diagram.png',
    'deployment_diagram.puml': 'deployment_diagram.png',
    'er_diagram.puml': 'er_diagram.png',
}

temp_dir = '/home/kassimi/Documents/work/eBank/temp_gen'
figures_dir = '/home/kassimi/Documents/work/eBank/figures'

for puml_file, png_file in puml_files.items():
    puml_path = os.path.join(temp_dir, puml_file)
    png_path = os.path.join(figures_dir, png_file)
    generate_png_from_puml(puml_path, png_path)

print("\nAll diagrams generated successfully!")
