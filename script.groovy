// String name, h|s|v type, String keyword1, blue|red|gray|yellow|green|purple color, bigstring content

abrev "1", "dice_1.png"
abrev "2", "dice_2.png"
abrev "3", "dice_3.png"
abrev "4", "dice_4.png"
abrev "5", "dice_5.png"
abrev "6", "dice_6.png"
image "images/${name.replace(" ","_")}.png", 0, 0, 780, 1088
image "back_${color}.png", 0, 0, 780, 1088
image "frame_${type}.png", 0, 0, 780, 1088

// name
font "Goldplay SemiBold", 60, "bold"
text name, 50, 654

font "Goldplay SemiBold", 36, "right"
def keywordsX = 780 - 50
def keywordsY = 615
text keyword1, keywordsX, keywordsY

color "#000000"
font "Goldplay SemiBold", 80
textImagesDefaults 0, 0, 0.6
text content, 100, 1480, 1000
