package de.unitrier.st.fst17.list;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.JPanel;

public class SDRaytracer extends JPanel
{
	private static final long serialVersionUID = 1L;
	int maxRec = 3;
	int width = 1000;
	int height = 1000;

	ExecutorService eservice = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
	List<Triangle> triangles;

	RGB[][] image = new RGB[width][height];

	float fovx = (float) 0.628;
	float fovy = (float) 0.628;
	int yAngleFactor = 4, xAngleFactor = -4;

	void profileRenderImage()
	{
		long end, start, time;

		renderImage(); // initialisiere Datenstrukturen, erster Lauf verfälscht
						// sonst Messungen

		for (int procs = 1; procs < 6; procs++)
		{
			maxRec = procs - 1;
			System.out.print(procs);
			for (int i = 0; i < 10; i++)
			{
				start = System.currentTimeMillis();

				renderImage();

				end = System.currentTimeMillis();
				time = end - start;
				System.out.print(";" + time);
			}
			System.out.println("");
		}
	}

	public void paint(Graphics g)
	{
		System.out.println("fovx=" + fovx + ", fovy=" + fovy + ", xangle=" + xAngleFactor + ", yangle=" + yAngleFactor);
		if (image == null)
			return;
		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++)
			{
				// g.setColor(image[i][j]);
				g.setColor(image[i][j].color());
				// zeichne einzelnen Pixel
				g.drawLine(i, height - j, i, height - j);
			}
	}

	SDRaytracer()
	{
		createScene();
		renderImage();
	}

	@SuppressWarnings("rawtypes")
	void renderImage()
	{

		Future[] futureList = new Future[width];
		RaytraceTask.tanFovx = Math.tan(fovx);
		RaytraceTask.tanFovy = Math.tan(fovy);
		for (int i = 0; i < width; i++)
		{
			futureList[i] = (Future) eservice.submit(new RaytraceTask(this, i));
		}

		for (int i = 0; i < width; i++)
		{
			try
			{
				RGB[] col = (RGB[]) futureList[i].get();
				for (int j = 0; j < height; j++)
					image[i][j] = col[j];
			} catch (InterruptedException e)
			{
			} catch (ExecutionException e)
			{
			}
		}
	}

	void createScene()
	{
		triangles = new ArrayList<Triangle>();
		SDRaytracer.addCube(triangles, 0, 35, 0, 10, 10, 10, new RGB(0.3f, 0, 0), 0.4f); // rot,
		// klein
		SDRaytracer.addCube(triangles, -70, -20, -20, 20, 100, 100, new RGB(0f, 0, 0.3f), .4f);
		SDRaytracer.addCube(triangles, -30, 30, 40, 20, 20, 20, new RGB(0, 0.4f, 0), 0.2f); // grün,
		// klein
		SDRaytracer.addCube(triangles, 50, -20, -40, 10, 80, 100, new RGB(.5f, .5f, .5f), 0.2f);
		SDRaytracer.addCube(triangles, -70, -26, -40, 130, 3, 40, new RGB(.5f, .5f, .5f), 0.2f);

		Matrix mRx = Matrix.createXRotation((float) (xAngleFactor * Math.PI / 16));
		Matrix mRy = Matrix.createYRotation((float) (yAngleFactor * Math.PI / 16));
		Matrix mT = Matrix.createTranslation(0, 0, 200);
		Matrix m = mT.mult(mRx).mult(mRy);
		m.print();
		m.apply(triangles);
	}

	public static void addCube(List<Triangle> triangles, int x, int y, int z, int w, int h, int d, RGB c, float sh)
	{ // front
		triangles.add(new Triangle(new Vec3D(x, y, z), new Vec3D(x + w, y, z), new Vec3D(x, y + h, z), c, sh));
		triangles.add(new Triangle(new Vec3D(x + w, y, z), new Vec3D(x + w, y + h, z), new Vec3D(x, y + h, z), c, sh));
		// left
		triangles.add(new Triangle(new Vec3D(x, y, z + d), new Vec3D(x, y, z), new Vec3D(x, y + h, z), c, sh));
		triangles.add(new Triangle(new Vec3D(x, y + h, z), new Vec3D(x, y + h, z + d), new Vec3D(x, y, z + d), c, sh));
		// right
		triangles.add(
				new Triangle(new Vec3D(x + w, y, z), new Vec3D(x + w, y, z + d), new Vec3D(x + w, y + h, z), c, sh));
		triangles.add(new Triangle(new Vec3D(x + w, y + h, z), new Vec3D(x + w, y, z + d),
				new Vec3D(x + w, y + h, z + d), c, sh));
		// top
		triangles.add(new Triangle(new Vec3D(x + w, y + h, z), new Vec3D(x + w, y + h, z + d), new Vec3D(x, y + h, z),
				c, sh));
		triangles.add(new Triangle(new Vec3D(x, y + h, z), new Vec3D(x + w, y + h, z + d), new Vec3D(x, y + h, z + d),
				c, sh));
		// bottom
		triangles.add(new Triangle(new Vec3D(x + w, y, z), new Vec3D(x, y, z), new Vec3D(x, y, z + d), c, sh));
		triangles.add(new Triangle(new Vec3D(x, y, z + d), new Vec3D(x + w, y, z + d), new Vec3D(x + w, y, z), c, sh));
		// back
		triangles.add(
				new Triangle(new Vec3D(x, y, z + d), new Vec3D(x, y + h, z + d), new Vec3D(x + w, y, z + d), c, sh));
		triangles.add(new Triangle(new Vec3D(x + w, y, z + d), new Vec3D(x, y + h, z + d),
				new Vec3D(x + w, y + h, z + d), c, sh));

	}

}