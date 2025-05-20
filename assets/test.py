from PIL import Image, ImageOps
import random

# Load the original image
original = Image.open("plant.png")

# Set canvas size and create a blank canvas
canvas_size = (500, 500)  # Width x Height
canvas = Image.new("RGBA", canvas_size, (255, 255, 255, 0))

# Function to generate random sections and transformations
def random_section_and_place(original, canvas):
    width, height = original.size

    # Randomly select a section of the image
    x1 = random.randint(0, width // 2)
    y1 = random.randint(0, height // 2)
    x2 = random.randint(width // 2, width)
    y2 = random.randint(height // 2, height)
    section = original.crop((x1, y1, x2, y2))  # Crop the section

    # Random transformations
    if random.choice([True, False]):
        section = section.rotate(random.randint(0, 360), expand=True)  # Random rotation
    if random.choice([True, False]):
        section = ImageOps.mirror(section)  # Horizontal flip

    # Resize section to a random size
    section = section.resize((random.randint(50, 150), random.randint(50, 150)))

    # Randomly place the section on the canvas
    canvas_width, canvas_height = canvas.size
    paste_x = random.randint(0, canvas_width - section.width)
    paste_y = random.randint(0, canvas_height - section.height)
    canvas.paste(section, (paste_x, paste_y), section)  # Paste with transparency

# Generate random placements
for _ in range(20):  # Number of random placements
    random_section_and_place(original, canvas)

# Save or display the result
canvas.show()
canvas.save("output_image.png")

