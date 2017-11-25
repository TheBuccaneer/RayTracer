package de.unitrier.st.fst17.list;

class IPoint
{
	final static float epsilon = 0.0001f;
	Triangle triangle;
	Vec3D ipoint;
	float dist;

	IPoint(Triangle tt, Vec3D ip, float d)
	{
		triangle = tt;
		ipoint = ip;
		dist = d;
	}

	static IPoint hitObject(SDRaytracer sd, Ray ray)
	{
		IPoint isect = new IPoint(null, null, -1);
		float idist = -1;
		for (Triangle t : sd.triangles)
		{
			IPoint ip = ray.intersect(t);
			if (ip.dist != -1)
				if ((idist == -1) || (ip.dist < idist))
				{ // save that intersection
					idist = ip.dist;
					isect.ipoint = ip.ipoint;
					isect.dist = ip.dist;
					isect.triangle = t;
				}
		}
		return isect; // return intersection point and normal
	}


}