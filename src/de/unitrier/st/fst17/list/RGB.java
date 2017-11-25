package de.unitrier.st.fst17.list;

import java.awt.Color;

class RGB
{
	float red, green, blue;
	Color color;
	Vec3D position;
	public final static RGB BLACK = new RGB(0.0f, 0.0f, 0.0f);
	static RGB mainLight = new RGB(new Vec3D(0, 100, 0), new RGB(0.1f, 0.1f, 0.1f));
	static RGB lights[] = new RGB[]
	{ mainLight, new RGB(new Vec3D(100, 200, 300), new RGB(0.5f, 0, 0.0f)),
			new RGB(new Vec3D(-100, 200, 300), new RGB(0.0f, 0, 0.5f))
			// ,new Light(new Vec3D(-100,0,0), new RGB(0.0f,0.8f,0.0f))
	};

	RGB(Vec3D pos, RGB rgb)
	{
		position = pos;
		red = rgb.red;
		green = rgb.green;
		blue = rgb.blue;
	}

	RGB(float r, float g, float b)
	{
		if (r > 1)
		{
			r = 1;
		} else if (r < 0)
		{
			r = 0;
		}
		if (g > 1)
		{
			g = 1;
		} else if (g < 0)
		{
			g = 0;
		}
		if (b > 1)
		{
			b = 1;
		} else if (b < 0)
		{
			b = 0;
		}
		red = r;
		green = g;
		blue = b;
	}

	Color color()
	{
		if (color != null)
			return color;
		color = new Color((int) (red * 255), (int) (green * 255), (int) (blue * 255));
		return color;
	}

	RGB addColors(SDRaytracer sd, RGB c2, float ratio)
	{
		return new RGB((red + c2.red * ratio), (green + c2.green * ratio), (blue + c2.blue * ratio));
	}

	RGB rayTrace(SDRaytracer sd, Ray ray, int rec)
	{
		if (rec > sd.maxRec)
			return RGB.BLACK;
		IPoint ip = IPoint.hitObject(sd, ray); // (ray, p, n, triangle);
		if (ip.dist > IPoint.epsilon)
			return lighting(sd, ray, ip, rec);
		else
			return RGB.BLACK;
	}

	RGB lighting(SDRaytracer sd, Ray ray, IPoint ip, int rec)
	{
		RGB ambient_color = new RGB(0.01f, 0.01f, 0.01f);
		Vec3D point = ip.ipoint;
		Triangle triangle = ip.triangle;
		RGB color = triangle.color.addColors(sd, ambient_color, 1);
		Ray shadow_ray = new Ray();
		for (RGB light : lights)
		{
			shadow_ray.start = point;
			shadow_ray.dir = light.position.minus(point).mult(-1);
			shadow_ray.dir.normalize();
			IPoint ip2 = IPoint.hitObject(sd, shadow_ray);
			if (ip2.dist < IPoint.epsilon)
			{
				float ratio = Math.max(0, shadow_ray.dir.dot(triangle.normal));
				color = color.addColors(sd, light, ratio);
			}
		}
		Ray reflection = new Ray();
		// R = 2N(N*L)-L) L ausgehender Vektor
		Vec3D L = ray.dir.mult(-1);
		reflection.start = point;
		reflection.dir = triangle.normal.mult(2 * triangle.normal.dot(L)).minus(L);
		reflection.dir.normalize();
		RGB rcolor = rayTrace(sd, reflection, rec + 1);
		float ratio = (float) Math.pow(Math.max(0, reflection.dir.dot(L)), triangle.shininess);
		color = color.addColors(sd, rcolor, ratio);
		return (color);
	}

}
