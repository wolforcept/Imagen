//package imagegenerator.templates;
//
//import imagegenerator.engine.Renderer;
//import imagegenerator.engine.Template;
//
//public class AgeOfWarBacks extends Template {
//
//	@Override
//	public int getWidth() {
//		return 1000;
//	}
//
//	@Override
//	public int getHeight() {
//		return 1000;
//	};
//
//	@Override
//	public boolean lineValid(String line) {
//		return true;
//	}
//
//	@Override
//	public Object[] readLine(String line) {
//		return new Object[] { line };
//	}
//
//	public void draw(Renderer r, String line) {
//		r.drawImage(line, 0, 0, getWidth(), getHeight());
//	}
//
//}
