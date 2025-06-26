interface Imagen {
     fun width(): Int
     fun height(): Int
     fun color(color: String)
     fun font(font: String, size: Int, attrs: String) // attrs: right, center, bold, italic
     fun font(font: String, size: Int)
     fun font(font: String)
     fun draw(imageId: String)
     fun draw(imageId: String, x: Int, y: Int)
     fun draw(imageId: String, x: Int, y: Int, w: Int, h: Int)
     fun text(string: String, x: Int, y: Int)
     fun text(string: String, x: Int, y: Int, w: Int)
     fun textImagesDefaults(x: Int, y: Int, scale: Float)
     fun abrev(abrev: String, path: String)
 }

 enum class TestEnum {
     Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday
 }

 class Point(x: Int, y: Int);

 class Script(
     val imagen: Imagen,
     val name: String,
     val i: Int,
     val f: Float,
     val b: Boolean,
     val test: TestEnum
 ) {

     val point = Point(0, 0)

     init {
         imagen.abrev("health", "heart.png")
         imagen.draw("$name.png", imagen.width() / 2, imagen.height() / 2, imagen.width() / 2, imagen.height() / 2)
         imagen.font("Arial", 35, "center")
         imagen.text("name is $name|", 50, 50, imagen.width())
     }

 }
