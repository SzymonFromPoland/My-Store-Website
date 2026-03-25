"""
Generates 16 product descriptions using GPT-4o, then generates images using gpt-image-1.
Saves descriptions to products.json and images to src/main/resources/static/images/products/

Usage:
    pip install openai
    python generate_products.py --openai-key YOUR_KEY
"""

import argparse
import base64
import json
import re
from pathlib import Path

import openai


IMAGES_DIR = Path(__file__).parent.parent / "src/main/resources/static/images/products"
OUTPUT_JSON = Path(__file__).parent / "products.json"


def generate_descriptions(client: openai.OpenAI) -> list[dict]:
    prompt = """Generate exactly 16 unique tech/electronics product listings for an online store.
Return a JSON array with exactly 16 objects. Each object must have:
- "name": short product name (e.g. "Sony WH-1000XM5")
- "description": 1-2 sentence product description (max 200 chars)
- "price": realistic price as a number (e.g. 349.99)
- "image_prompt": a prompt for a clean product photo on white background

Cover a variety of categories: headphones, keyboards, monitors, phones, cameras, smartwatches, speakers, laptops, tablets, mice, chargers, cables, webcams, microphones, SSDs, game controllers.

Return ONLY the raw JSON array, no markdown, no explanation."""

    print("Generating product descriptions with GPT-4o...")
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[{"role": "user", "content": prompt}]
    )

    raw = response.choices[0].message.content.strip()
    raw = re.sub(r"^```(?:json)?\n?", "", raw)
    raw = re.sub(r"\n?```$", "", raw)

    products = json.loads(raw)
    print(f"Generated {len(products)} products.")
    return products


def generate_images(client: openai.OpenAI, products: list[dict]) -> list[dict]:
    IMAGES_DIR.mkdir(parents=True, exist_ok=True)

    for i, product in enumerate(products):
        filename = re.sub(r"[^a-z0-9]+", "_", product["name"].lower()).strip("_") + ".png"
        filepath = IMAGES_DIR / filename

        print(f"[{i+1}/16] Generating image for: {product['name']}")

        try:
            response = client.images.generate(
                model="gpt-image-1",
                prompt=product["image_prompt"],
                size="1024x1024",
                quality="medium",
                n=1,
            )

            image_data = response.data[0].b64_json
            with open(filepath, "wb") as f:
                f.write(base64.b64decode(image_data))

            product["image_file"] = filename
            print(f"  Saved: {filepath}")

        except Exception as e:
            print(f"  ERROR generating image for {product['name']}: {e}")
            product["image_file"] = None

    return products


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument("--openai-key", required=True, help="OpenAI API key")
    parser.add_argument("--skip-descriptions", action="store_true", help="Load existing products.json and only regenerate images")
    args = parser.parse_args()

    client = openai.OpenAI(api_key=args.openai_key)

    if args.skip_descriptions and OUTPUT_JSON.exists():
        print("Loading existing products from products.json...")
        with open(OUTPUT_JSON) as f:
            products = json.load(f)
    else:
        products = generate_descriptions(client)

    products = generate_images(client, products)

    with open(OUTPUT_JSON, "w") as f:
        json.dump(products, f, indent=2)

    print(f"\nDone! Saved {len(products)} products to {OUTPUT_JSON}")
    for p in products:
        print(f"  - {p['name']} (${p['price']}) -> {p.get('image_file')}")


if __name__ == "__main__":
    main()
