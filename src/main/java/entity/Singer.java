package entity;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import static javax.persistence.GenerationType.AUTO;

@Entity
public class Singer implements Serializable {

	@Id
	@GeneratedValue(strategy = AUTO)
	private Long id;

	@Version
	private int version;

	@NotBlank(message="{validation.firstname.NotBlank.message}")
	@Size(min=2, max=60, message="{validation.firstname.Size.message}")
	private String firstName;

	@NotBlank(message="{validation.lastname.NotBlank.message}")
	@Size(min=1, max=40, message="{validation.lastname.Size.message}")
	private String lastName;

	@NotNull(message="{validation.birthDate.NotBlank.message}")
	@Temporal(TemporalType.DATE)
	private Date birthDate;

	private String description;

	@Basic(fetch= FetchType.LAZY)
	@Lob
	private byte[] photo;

	public Long getId() {
		return id;
	}

	public int getVersion() {
		return version;
	}

	public String getFirstName() {
		return firstName;
	}


	public String getLastName() {
		return lastName;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}
//
//	@Transient
//	public String getBirthDateString() {
//		if (birthDate != null) return new SimpleDateFormat("dd.MM.yyyy").format(birthDate);
//		return "";
//	}

	@Override
	public String toString() {
		return "Singer - Id: " + id + ", First name: " + firstName
				+ ", Last name: " + lastName + ", Birthday: " + birthDate
				+ ", Description: " + description;
	}
}
