package com.globocom.controller;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.globocom.model.User;
import com.globocom.service.UserService;
import com.globocom.util.Constants;
import com.globocom.util.ContentConstants;
import com.globocom.util.FileZipUtil;
import com.globocom.util.GenerateRandomString;

@CrossOrigin(origins = {"/**"}, maxAge = 4800, allowCredentials = "false")
@RestController
public class UploadController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);
	FileZipUtil unZip = new FileZipUtil();
	
	@Autowired
	UserService userservice;
	
	@PostMapping("/upload")
	   public ResponseEntity<?> upload(@RequestParam("file") List<MultipartFile> files,HttpServletRequest request,HttpServletResponse response) {
			
			System.out.println("file.getOriginalFilename()" +files.get(0).getOriginalFilename());
			System.out.println("file.size" +files.get(0).getSize());
			try {
				GenerateRandomString randValue = new GenerateRandomString();
				String directoryPath = File.separator + "content" + File.separator + randValue.getAlphaNumeric(20) + File.separator;
				File newFile = new File(Constants.SERVER_FILE_PATH + directoryPath);
				if (!newFile.exists()) {
					newFile.mkdirs();
					//LOGGER.info("newFile.mkdirs(); " + newFile.mkdirs());
				}
				boolean previewFileStatus = false;
				if (null != files && files.size() > 0) {
					for (MultipartFile multipartFile : files) {
						String fileName = "";
						
						
						try {
							fileName = multipartFile.getOriginalFilename();
							System.out.println("contentDetail fileName: :" + fileName);
							if (fileName.lastIndexOf(ContentConstants.ZIP_FILE_EXTENSION) > 0) {
								System.out.println("READ ZIP File:" + fileName);

							} else if (fileName.lastIndexOf(ContentConstants.RAR_FILE_EXTENSION) > 0) {
								System.out.println("READ RAR File:" + fileName);

							}
							if (!"".equalsIgnoreCase(fileName)) {
								multipartFile.transferTo(new File(Constants.SERVER_FILE_PATH + directoryPath + fileName));

							}
							unZip.unZipIt(Constants.SERVER_FILE_PATH + directoryPath + fileName, Constants.SERVER_FILE_PATH + directoryPath);
							
							boolean checkBulk = isBulkFile(Constants.SERVER_FILE_PATH + directoryPath);
							
							if (checkBulk) {
								LOGGER.info("BULK FILES UPLOADED FOUND SUCSESSFULLY");
								File directory = new File(Constants.SERVER_FILE_PATH + directoryPath);
								// get all the files from a directory
								File[] fList = directory.listFiles();
								for (File file : fList) {
									if (file.isFile()) {
										LOGGER.info("BULK FILES UPLOADED FOUND SUCSESSFULLY :file.getAbsolutePath() :" + file.getAbsolutePath());
									} else if (file.isDirectory()) {
										//Content content = new Content();
										LOGGER.info("BULK FILES UPLOADED FOUND SUCSESSFULLY Directory:file.getAbsolutePath() :" + file.getAbsolutePath() + "file.getName() :" + file.getName());
										previewFileStatus = createPreviewFiles(directoryPath + file.getName() + File.separator);
										if (previewFileStatus) {
											LOGGER.info("PREVIEW FILE GENERATED SUCSESSFULLY");
											/*content = addContentList(contentTypeId, contentProviderId, directoryPath + file.getName() + File.separator);
											contentList.add(content);
											if (contentList.size() > 0) {
												LOGGER.info("CONTENT FILES ADDED SUCSESSFULLY");
											}*/
										} else {
											LOGGER.info("PREVIEW FILE NOT GENERATED SUCSESSFULLY");
											//message = "PREVIEW FILE NOT GENERATED SUCSESSFULLY.";
										}

									}
								}
							} else {
								LOGGER.info("SINGLE FILES UPLOADED FOUND SUCSESSFULLY");
								previewFileStatus = createPreviewFiles(directoryPath);

								if (previewFileStatus) {
									LOGGER.info("PREVIEW FILE GENERATED SUCSESSFULLY");
									// One by one Read UnZip File and Add to
									// ContentList
									/*Content content = new Content();
									content = addContentList(contentTypeId, contentProviderId, directoryPath);
									contentList.add(content);
									if (contentList.size() > 0) {
										LOGGER.info("CONTENT FILES ADDED SUCSESSFULLY");
									}*/
								} else {
									LOGGER.info("PREVIEW FILE NOT GENERATED SUCSESSFULLY");
									//message = "PREVIEW FILE NOT GENERATED SUCSESSFULLY.";
								}

							}
							
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
					
					
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			
			return ResponseEntity.ok().body("New User has been Registered with ID:");
	  }
	
	private boolean isBulkFile(String directoryPath) {
		boolean flag = false;
		LOGGER.info("createPreviewFiles : Content Folder File List directoryPath:" + directoryPath);
		File directory = new File(directoryPath);
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				LOGGER.info("Content Folder File List :" + file.getName());
				String unZipFileName = file.getName();

				String zipFolderPath = unZipFileName.substring(0, unZipFileName.lastIndexOf("."));
				if (unZipFileName.lastIndexOf(ContentConstants.ZIP_FILE_EXTENSION) > 0) {
					LOGGER.info("Content Folder File READ ZIP File:" + unZipFileName);
					unZip.unZipIt(directoryPath + unZipFileName, directoryPath + zipFolderPath);
					flag = true;
				}
			}
		}
		return flag;

	}
	
	private boolean createPreviewFiles(String directoryPath) {

		boolean flag = false;
		LOGGER.info("createPreviewFiles : Content Folder File List directoryPath:" + directoryPath);
		File directory = new File(Constants.SERVER_FILE_PATH + directoryPath);
		File[] fList = directory.listFiles();

		for (File file : fList) {
			if (file.isFile()) {
				LOGGER.info("Content Folder File List :" + file.getName());
				String unZipFileName = file.getName();
				if (unZipFileName.lastIndexOf(ContentConstants.ZIP_FILE_EXTENSION) > 0) {
					LOGGER.info("Content Folder File READ ZIP File:" + unZipFileName);

				} else if (unZipFileName.lastIndexOf(ContentConstants.RAR_FILE_EXTENSION) > 0) {
					LOGGER.info("Content Folder File READ RAR File:" + unZipFileName);

				} else if (unZipFileName.lastIndexOf(ContentConstants.EXCEL_FILE_EXTENSION) > 0) {
					LOGGER.info("Content Folder File READ RAR File:" + unZipFileName);

				} else {
					LOGGER.info("Content Folder File READ CONETNT Files:" + unZipFileName);

					String transcodeFile = unZipFileName.substring(0, unZipFileName.lastIndexOf("."));

					LOGGER.info("Content Folder File READ transcodeFile  Files:" + transcodeFile);

					try {
						File inputFile = null;
						try {
							LOGGER.info("Content Folder File READ CONETNT Files  " + Constants.SERVER_FILE_PATH + directoryPath + File.separator + unZipFileName);
							inputFile = new File(Constants.SERVER_FILE_PATH + directoryPath + File.separator + unZipFileName);

							LOGGER.info("Content Folder File READ CONETNT Files  Transcoding Preview Files  found:" + inputFile);

						} catch (Exception e) {
							LOGGER.info("Content Folder File READ CONETNT Files  Transcoding Preview Files Exception found:" + e);
						}

						LOGGER.info("Content Folder File READ CONETNT Files:" + ContentConstants.PREVIEW_FILE_WIDTH_100X100 + ContentConstants.PREVIEW_FILE_HEIGHT_100X100 + ":"
								+ ContentConstants.IMAGE_FILE_PNG + ":" + transcodeFile + ":" + Constants.SERVER_FILE_PATH + directoryPath);

						// Transcoding Content Files 500x500.png
//						PreviewImage.resizeImage(inputFile, ContentConstants.CONTENT_FILE_WIDTH_400X400, ContentConstants.CONTENT_FILE_HEIGHT_400X400, ContentConstants.IMAGE_FILE_PNG, transcodeFile,
//								Constants.SERVER_FILE_PATH + directoryPath);

						// Input File for creating Preview
						try {
							LOGGER.info("Content Folder File READ CONETNT Files for Making Preview  " + Constants.SERVER_FILE_PATH + directoryPath + File.separator + transcodeFile
									+ ContentConstants.CONTENT_FILE_EXTENSION_400X400);
							inputFile = new File(Constants.SERVER_FILE_PATH + directoryPath + File.separator + transcodeFile + ContentConstants.CONTENT_FILE_EXTENSION_400X400);

							LOGGER.info("Content Folder File READ CONETNT Files  Transcoding Preview Files  found for Making Preview:" + inputFile);

						} catch (Exception e) {
							LOGGER.info("Content Folder File READ CONETNT Files  Transcoding Preview Files Exception found for Making Preview:" + e);
						}

						// Transcoding Preview Files 150x150.gif
//						PreviewImage.previewImage(inputFile, ContentConstants.PREVIEW_FILE_WIDTH_150X150, ContentConstants.PREVIEW_FILE_HEIGHT_150X150, ContentConstants.IMAGE_FILE_PNG, transcodeFile,
//								Constants.SERVER_FILE_PATH + directoryPath);

						LOGGER.info("Content Folder File READ CONETNT Files  Transcoding Preview Files 150x150.gif:" + unZipFileName);

						// Transcoding Preview Files 100x100.gif
//						PreviewImage.previewImage(inputFile, ContentConstants.PREVIEW_FILE_WIDTH_100X100, ContentConstants.PREVIEW_FILE_HEIGHT_100X100, ContentConstants.IMAGE_FILE_PNG, transcodeFile,
//								Constants.SERVER_FILE_PATH + directoryPath);

						LOGGER.info("Content Folder File READ CONETNT Files  Transcoding Preview Files 100x100.gif:" + unZipFileName + " directoryPath :" + directoryPath);
						flag = true;
					} catch (Exception e) {
						LOGGER.info("Content Folder File READ CONETNT Files  Transcoding Preview Files 150x150.gif:" + unZipFileName + " Exception :" + e);
						e.printStackTrace();
					}

				}
			}
		}
		return flag;

	}
}