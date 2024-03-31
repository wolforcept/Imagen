package imagegenerator.templates;

import java.util.Collections;
import java.util.LinkedList;

import imagegenerator.engine.Renderer;
import imagegenerator.engine.Template;

public class Scouts2 extends Template {

	int squaresize = 380;

	public Scouts2() {
		super(3508, 2480);
	}

	@Override
	public boolean lineValid(String line) {
		return true;
	}

	public void draw(Renderer r, String runName, String a) {

		for (int[] dxy : new int[][] { { 0, 0 }, { width / 2, height / 2 }, { width / 2, 0 }, { 0, height / 2 } }) {

			LinkedList<Integer> ii = new LinkedList<>();
			ii.add(1);
			ii.add(2);
			ii.add(2);
			ii.add(3);
			ii.add(4);
			for (int i = 0; i < 3; i++) {
				ii.add(1 + (int)( Math.random() * 4.0));
			}
			Collections.shuffle(ii);

			r.drawImage("0", dxy[0], dxy[1]);

			for (int i = 0; i < 4; i++)
				r.drawImage("" + ii.pop(), 117 + dxy[0] + i * squaresize, 117 + dxy[1], squaresize, squaresize);

			for (int i = 0; i < 4; i++)
				r.drawImage("" + ii.pop(), 117 + dxy[0] + i * squaresize, 117 + dxy[1] + squaresize, squaresize, squaresize);

		}
	}

}
