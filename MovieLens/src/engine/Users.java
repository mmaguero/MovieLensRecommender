package engine;

public class Users {

	private int idUser;
	private double averageRating;

	public Users() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Users(int idUser, double averageRating) {
		super();
		this.idUser = idUser;
		this.averageRating = averageRating;
	}

	public int getIdUser() {
		return idUser;
	}

	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}

	public double getAverageRating() {
		return averageRating;
	}

	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}

}
