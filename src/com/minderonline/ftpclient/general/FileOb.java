package com.minderonline.ftpclient.general;

/** This Class Object is for Holding Temporary Files 
 * from the Filemanager. You Can use it with the Local Files or
 * with the Remote Files from an FTP - Server.
 * **/

public class FileOb {

	String FileName;
	String FilePath;
	Boolean isDirectory;
	Boolean Checked;

	// Instantiate an Filled object
	public FileOb(String Filename, String FilePath, Boolean Checked,
			Boolean isDirectory) {
		this.FileName = Filename;
		this.FilePath = FilePath;
		this.Checked = Checked;
		this.isDirectory = isDirectory;
		
	}

	// Instantiate an empty Object
	public FileOb() {

		this.FileName = null;
		this.FilePath = null;
		this.Checked = false;
		this.isDirectory = false;
	}

	// Getter Methods
	public String getFileName() {
		return this.FileName;
	}

	public String getFilePath() {
		return this.FilePath;

	}

	public Boolean isChecked() {

		return this.Checked;
	}

	public Boolean isDirectory() {
		return this.isDirectory;

	}

	// Setter Methods
	public void setFilename(String FileName) {
		this.FileName = FileName;

	}

	public void setFilePath(String FilePath) {
		this.FilePath = FilePath;

	}

	public void setChecked(Boolean Checked) {
		this.Checked = Checked;

	}

	public void isDirectory(Boolean isDirectory) {

		this.isDirectory = isDirectory;
	}

}
