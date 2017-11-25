package de.unitrier.st.fst17.list;

import java.util.concurrent.Callable;

class RaytraceTask implements Callable<RGB[]>
{
	SDRaytracer tracer;
	int i;
	int rayPerPixel = 1;
	static double tanFovx;
	static double tanFovy;

	RaytraceTask(SDRaytracer t, int ii)
	{
		tracer = t;
		i = ii;
	}

	public RGB[] call()
	{
		RGB[] col = new RGB[tracer.height];
		for (int j = 0; j < tracer.height; j++)
		{
			tracer.image[i][j] = new RGB(0, 0, 0);
			for (int k = 0; k < rayPerPixel; k++)
			{
				double di = i + (Math.random() / 2 - 0.25);
				double dj = j + (Math.random() / 2 - 0.25);
				if (rayPerPixel == 1)
				{
					di = i;
					dj = j;
				}
				Ray eye_ray = new Ray();
				eye_ray.setStart(0, 0, 0); // ro
				eye_ray.setDir((float) (((0.5 + di) * tanFovx * 2.0) / tracer.width - tanFovx),
						(float) (((0.5 + dj) * tanFovy * 2.0) / tracer.height - tanFovy), (float) 1f); // rd
				eye_ray.normalize();
				col[j] = tracer.image[i][j].addColors(tracer, tracer.image[i][j].rayTrace(tracer, eye_ray, 0),
						1.0f / rayPerPixel);
			}
		}
		return col;
	}
}