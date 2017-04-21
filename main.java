
public class main {

	public static void main(String[] args) {
		String filename = "filname.jpg";
		String saveFilename = "saveName.jpg";
		double x = 0.80;
		
		ImageProcessor ip = new ImageProcessor(filename);
		Picture reducedPicture = ip.reduceWidth(x);
		reducedPicture.save(saveFilename);
	}

}
