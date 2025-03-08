package com.project.classistant;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by pronoymukherjee on 04/04/17.
 */

public class FileController {
    Context context;
    Boolean isLoginValid = false;

    public FileController(Context context) {
        this.context = context;
    }

    /**
     * NOTE: This is the method which is creating the local account of the student in a File.
     * @param studentInfo: The details of the student.
     */
    void CreateAccountStudent(Bundle studentInfo) {
        String data = "";
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(Constant.ACCOUNT_FILENAME, Context.MODE_PRIVATE);
            data = Constant.STUDENT_NAME + ":";
            data += studentInfo.getString(Constant.STUDENT_NAME) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.STUDENT_ROLL + ":";
            data += studentInfo.getString(Constant.STUDENT_ROLL) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.STUDENT_EMAIL + ":" + studentInfo.getString(Constant.STUDENT_EMAIL) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.STUDENT_PASSWORD + ":" + studentInfo.getString(Constant.STUDENT_PASSWORD) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.STUDENT_STREAM + ":" + studentInfo.getString(Constant.STUDENT_STREAM) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.STUDENT_SECTION + ":" + studentInfo.getString(Constant.STUDENT_SECTION) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.STUDENT_START_YR + ":" + studentInfo.getString(Constant.STUDENT_START_YR) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.STUDENT_END_YR + ":" + studentInfo.getString(Constant.STUDENT_END_YR) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.COLLEGE_NAME + ":" + studentInfo.getString(Constant.COLLEGE_NAME) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            data = Constant.DATE_BIRTH_STUDENT + ":" + studentInfo.getString(Constant.DATE_BIRTH_STUDENT) + ";";
            fileOutputStream.write(data.getBytes());
            data = "";
            fileOutputStream.close();
            JSONObject studentValues = new JSONObject();
            JSONArray where = new JSONArray();
            JSONArray values = new JSONArray();
            where.put(Constant.NAME);
            where.put(Constant.LOGIN_EMAIL);
            where.put(Constant.PASSWORD_HASH);
            where.put(Constant.ROLL_NUMBER);
            where.put(Constant.DATE_BIRTH_STUDENT);
            where.put(Constant.START_YEAR);
            where.put(Constant.END_YEAR);
            where.put(Constant.COLLEGE_NAME);
            where.put(Constant.BSSID);
            where.put(Constant.STREAM);
            where.put(Constant.SECTION);
            studentValues.put(Constant.TYPE, Constant.TYPE_INSERT);
            studentValues.put(Constant.TABLE_NAME, Constant.TABLE_STUDENT_METADATA);
            values.put(studentInfo.getString(Constant.STUDENT_NAME));
            values.put(studentInfo.getString(Constant.STUDENT_EMAIL));
            values.put(studentInfo.getString(Constant.STUDENT_PASSWORD));
            values.put(studentInfo.getString(Constant.STUDENT_ROLL));
            values.put(studentInfo.getString(Constant.DATE_BIRTH_STUDENT));
            values.put(studentInfo.getString(Constant.STUDENT_START_YR));
            values.put(studentInfo.getString(Constant.STUDENT_END_YR));
            values.put(studentInfo.getString(Constant.COLLEGE_NAME));
            //values.put(studentInfo.getString(Constant.BSSID));
            values.put(studentInfo.getString(Constant.STUDENT_STREAM));
            values.put(studentInfo.getString(Constant.STUDENT_SECTION));
            studentValues.put(Constant.WHERE, where);
            studentValues.put(Constant.VALUE, values);
            JSONObject object = QueryCreator.createQuery(studentValues);
            CloudSync cloudSync=new CloudSync();
            String arr[]={Constant.CHOICE_SYNC_CLOUD+"",object.toString()};
            cloudSync.execute(arr);
            syncCloud(object);//uploading the data to CLOUD.
        } catch (IOException e) {
            Message.logMessages("IOException: ", e.toString());
        } catch (Exception e) {
            Message.logMessages("EXCEPTION: ", e.toString());
        }

    }

    /**
     * NOTE: This is the method which is creating the local account of the teacher in the
     * file and then inserting it into the TEACHER_METADATA.
     * @param teacherInfo: This is the details of the teacher.
     */
    void createAccountTeacher(Bundle teacherInfo) {
        String data="";
        try{
            FileOutputStream fileOutputStream=context.openFileOutput(Constant.ACCOUNT_FILENAME,Context.MODE_PRIVATE);
            data=Constant.TEACHER_NAME+":";
            data+=teacherInfo.getString(Constant.TABLE_NAME)+";";
            fileOutputStream.write(data.getBytes());
            data="";
            data=Constant.TEACHER_PHONE+":";
            data+=teacherInfo.getString(Constant.TEACHER_PHONE)+";";
            fileOutputStream.write(data.getBytes());
            data="";
            data=Constant.TEACHER_EMAIL+":";
            data+=teacherInfo.getString(Constant.TEACHER_EMAIL)+";";
            fileOutputStream.write(data.getBytes());
            Constant.TEACHER_EMAIL_VALUE=teacherInfo.getString(Constant.TEACHER_EMAIL);
            data="";
            data=Constant.PASSWORD_HASH+":";
            data+=teacherInfo.getString(Constant.PASSWORD_HASH)+";";
            fileOutputStream.write(data.getBytes());
            data="";
            data=Constant.TEACHER_DEPT+":";
            data+=teacherInfo.getString(Constant.TEACHER_DEPT)+";";
            fileOutputStream.write(data.getBytes());
            data="";
            data=Constant.TEACHER_COLLEGE_NAME+":";
            data+=teacherInfo.getString(Constant.TEACHER_COLLEGE_NAME)+";";
            fileOutputStream.write(data.getBytes());
            fileOutputStream.close();// closing the File OutputStream.
            //TODO: Sync data to cloud.
            try {
                JSONArray values = new JSONArray();
                values.put(teacherInfo.getString(Constant.TEACHER_NAME));
                values.put(teacherInfo.getString(Constant.TEACHER_EMAIL));
                values.put(teacherInfo.getString(Constant.PASSWORD_HASH));
                values.put(teacherInfo.getString(Constant.TEACHER_PHONE));
                values.put(teacherInfo.getString(Constant.TEACHER_DEPT));
                values.put(teacherInfo.getString(Constant.TEACHER_COLLEGE_NAME));
                JSONArray where=new JSONArray();
                where.put(Constant.TEACHER_NAME);
                where.put(Constant.TEACHER_EMAIL);
                where.put(Constant.PASSWORD_HASH);
                where.put(Constant.TEACHER_PHONE);
                where.put(Constant.TEACHER_DEPT);
                where.put(Constant.TEACHER_COLLEGE_NAME);
                JSONObject teacherData=new JSONObject();
                teacherData.put(Constant.VALUE,values);
                teacherData.put(Constant.WHERE,where);
                JSONObject query=QueryCreator.createQuery(teacherData);
                String arr[]={Constant.CHOICE_SYNC_CLOUD+"",query.toString()};
                CloudSync sync=new CloudSync();
                sync.execute(arr);// Syncing the data to the Teacher MetaData.
            }
            catch (JSONException e){
                Message.logMessages("ERROR: ",e.toString());
            }

        }
        catch (IOException e){
            Message.logMessages("ERROR: ",e.toString());
        }
    }

    /**
     * This is the method which is used to insert the data to LoginMetadata.
     * @param account: Either Teacher or Student.
     * @param email: Email id.
     * @param passwordHash: Password Hash.
     */
    void createLoginDetails(String account, String email, String passwordHash) {
        try {
            FileOutputStream fileOutputStream = context.openFileOutput(Constant.LOGIN_FILENAME, Context.MODE_PRIVATE);
            fileOutputStream.write((Constant.ACCOUNT + ":" + account + ";").getBytes());
            fileOutputStream.write((Constant.LOGIN_EMAIL + ":" + email + ";").getBytes());
            fileOutputStream.write((Constant.PASSWORD_HASH + ":" + passwordHash + ";").getBytes());
            fileOutputStream.close();
            JSONObject Login = new JSONObject();
            JSONObject where = new JSONObject();//where clause.
            JSONObject values=new JSONObject(); //values
            if(account.equals(Constant.ACCOUNT_TEACHER)){
                account="T";
            }
            else if(account.equals(Constant.ACCOUNT_STUDENT)){
                account="S";
            }
            Login.put(Constant.TYPE, Constant.TYPE_INSERT);
            Login.put(Constant.TABLE_NAME, Constant.LOGIN_METADATA);
            values.put(Constant.ACCOUNT, account);
            values.put(Constant.LOGIN_EMAIL, email);
            values.put(Constant.PASSWORD_HASH, passwordHash);

            where.put(Constant.ACCOUNT,Constant.ACCOUNT);
            where.put(Constant.LOGIN_EMAIL,Constant.LOGIN_EMAIL);
            where.put(Constant.PASSWORD_HASH,Constant.PASSWORD_HASH);
            Login.put(Constant.WHERE, where);
            Login.put(Constant.VALUE,values);
            JSONObject object = QueryCreator.createQuery(Login);
            CloudSync cloudSync = new CloudSync();
            String arr[] = {Constant.CHOICE_SYNC_CLOUD + "", object.toString()};
            cloudSync.execute(arr);// Insert into Login Metadata.
        } catch (IOException e) {
            Message.logMessages("IOException: ", e.toString());
        } catch (Exception e) {
            Message.logMessages("EXCEPTION: ", e.toString());
        }
    }

    /**
     * NOTE:This the method to check the logged in account.
     * @param account: The account.
     * @param email: The login Email.
     * @param passwordHash: The Login password.
     * @return: true or false based on whether its logged in or not.
     */
    protected boolean checkLogin(String account, String email, String passwordHash) {
        File file = new File(Constant.LOGIN_FILENAME);
        try {
            if (file.exists()) {
                FileInputStream fileInputStream = context.openFileInput(Constant.LOGIN_FILENAME);
                Scanner scanner = new Scanner(fileInputStream);
                String data = "";
                while (scanner.hasNext()) {
                    data += scanner.nextLine();
                }
                fileInputStream.close();
                String _parts[] = data.split(";");
                String acc_part = _parts[0];
                String _account = acc_part.split(":")[1];
                String email_part = _parts[1];
                String _email = email_part.split(":")[1];
                String password_part = _parts[2];
                String _passwordHash = password_part.split(":")[1];
                if (account.equalsIgnoreCase(_account) && email.equals(_email) && passwordHash.equals(_passwordHash))
                    return true;
                else if (!passwordHash.equals(_passwordHash)) {
                    Message.toastMessage(context, "Incorrect Password!", "long");
                    return false;
                }
            } else {
                JSONObject loginCheck = new JSONObject();
                loginCheck.put(Constant.TYPE, Constant.TYPE_SELECT);
                loginCheck.put(Constant.LOGIN_EMAIL, email);
                loginCheck.put(Constant.PASSWORD_HASH, passwordHash);
                loginCheck.put(Constant.ACCOUNT, account);
                JSONObject queryObject = QueryCreator.createQuery(loginCheck);
                CloudSync cloudSync = new CloudSync();
                String arr[] = {Constant.CHOICE_GET_CLOUDATA + "", queryObject.toString()};
                cloudSync.execute(arr);
                return isLoginValid;
            }
        } catch (IOException e) {
            Message.logMessages("IOException: ", e.toString());
        } catch (Exception e) {
            Message.logMessages("EXCEPTION: ", e.toString());
        }
        return false;
    }

    /**
     * NOTE: This is the method to sync or the data to the cloud.
     * @param data: The query which is to be executed in the Server.
     */
    private void syncCloud(JSONObject data) {
        try {
            HTTPHandler httpHandler = new HTTPHandler(Constant.URL_QUERY, 100000, true, true, "POST");
            httpHandler.HttpPost(data);
        } catch (IOException e) {
            Message.logMessages("ERROR: ", e.toString());
        }
    }

    /**
     * NOTE: This is the method which will return the value after executing the query in the server.
     * @param jsonObject: The Query to be executed.
     * @return: The result after executing the query.
     */
    private String getDataCloud(JSONObject jsonObject) {
        String reply = "";
        try {
            HTTPHandler httpHandler = new HTTPHandler(Constant.URL_QUERY, 10000, true, true, "POST");
            httpHandler.HttpPost(jsonObject);
            reply = httpHandler.getReplyData();
            httpHandler.closeConnection();
        } catch (IOException e) {
            Message.logMessages("ERROR: ", e.toString());
        }
        return reply;
    }/*
    private boolean isLoginValid(JSONObject jsonObject){
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(Constant.QUERY_REPLY);
            JSONArray array = jsonArray.getJSONArray(0);
            int id = Integer.parseInt(array.getString(0));
            if (id > 0) {
                return true;
            } else {
                Message.toastMessage(context, "ERROR.", "");
            }
        }
        catch (JSONException e){
            Message.logMessages("ERROR: ",e.toString());
        }
        return false;
    }*/

    /**
     * This the Async task class to sync and get cloud data.
     */
    public class CloudSync extends AsyncTask<String, Void, String> {
        int choice;

        @Override
        protected String doInBackground(String... data) {
            String returnData = "";
            choice = Integer.parseInt(data[0]);
            try {
                switch (choice) {
                    case 1:
                        syncCloud(new JSONObject(data[1]));
                        break;
                    case 2:
                        returnData = getDataCloud(new JSONObject(data[1]));
                        break;
                }
            } catch (JSONException e) {
                Message.logMessages("ERROR: ", e.toString());
            }
            return returnData;
        }

        @Override
        protected void onPostExecute(String cloudData) {
            try {
                switch (choice) {
                    case 2:
                        JSONObject object;
                        if (!cloudData.equals("")) {
                            object = new JSONObject(cloudData);
                            JSONArray jsonArray = object.getJSONArray(Constant.QUERY_REPLY);
                            JSONArray array = jsonArray.getJSONArray(0);
                            int id = Integer.parseInt(array.getString(0));
                            if (id > 0) {
                                isLoginValid = true;
                            } else {
                                Message.toastMessage(context, "ERROR.", "");
                            }
                        }
                }
            } catch (JSONException e) {
                Message.logMessages("ERROR: ", e.toString());
            }
        }
    }
}