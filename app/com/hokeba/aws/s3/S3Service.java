package com.hokeba.aws.s3;

import com.hokeba.http.HTTPRequest3;

import java.io.*;
import com.amazonaws.auth.*;
import com.amazonaws.services.s3.*;
import com.amazonaws.services.s3.model.*;

import play.Logger;
import play.Play;

public class S3Service extends HTTPRequest3 {
    public static final String AWS_S3_BUCKET = Play.application().configuration().getString("whizliz.aws.s3_bucket", "");
    public static final String AWS_ACCESS_KEY = Play.application().configuration().getString("whizliz.aws.access_key", "");
    public static final String AWS_SECRET_KEY = Play.application().configuration().getString("whizliz.aws.secret_key", "");
    
    public static S3Service instance;
    public static AmazonS3 amazonS3;
    
    public static boolean enabled() {
    	return (Play.application().configuration().keys().contains("whizliz.aws.access_key") &&
    			Play.application().configuration().keys().contains("whizliz.aws.secret_key") &&
    			Play.application().configuration().keys().contains("whizliz.aws.s3_bucket"));
    }
    
    public static S3Service getInstance() {
		if (instance == null) {
			instance = new S3Service();
			AWSCredentials awsCredentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
			amazonS3 = new AmazonS3Client(awsCredentials);
		}
		return instance;
    }
    
    public void saveObject(String filename, File file) {
    	try {
    		//initialize metadata
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setCacheControl("max-age=2592000");
			
			//initialize object
        	PutObjectRequest putObjectRequest = new PutObjectRequest(AWS_S3_BUCKET, filename, file);
//            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
            putObjectRequest.setMetadata(metadata);
            
            //put object to s3
            amazonS3.putObject(putObjectRequest); // upload file
            Logger.info("[S3] Upload file " + file.getName() + " to " + filename);
		} catch (Exception e) {
			// TODO: handle exception
			Logger.info(e.getMessage());
		}
    }
    
    public void deleteObject(String filename) {
    	try {
        	amazonS3.deleteObject(AWS_S3_BUCKET, filename);
            Logger.info("[S3] Delete file " + filename);
		} catch (Exception e) {
			// TODO: handle exception
			Logger.info(e.getMessage());
		}
    }
}
