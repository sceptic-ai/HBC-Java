package tech.pod.dataset.storageprovider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.nio.*;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
//S3Provider acts as a basic StorageProvider, uploading, getting, and removing objects from Amazon AWS S3

public class S3Provider implements  StorageProviderInterface{
    int maxActiveThreads;
    String bucketName;
    String tempPath;
    InetSocketAddress daemonIP;
    ServerSocketChannel serverSocket;
    InetSocketAddress commandIP;
    int defaultBufferSize;
    String saveLocation;
    boolean active = false;
    ConcurrentHashMap < String, String > authCodes = new ConcurrentHashMap < String, String > ();
    ConcurrentLinkedDeque<SocketChannel> socketQueue = new ConcurrentLinkedDeque<SocketChannel>();
    S3Provider(String bucketName, InetSocketAddress daemonIP, InetSocketAddress commandIP, int defaultBufferSize, int maxActiveThreads) {
        this.bucketName = bucketName;
        this.daemonIP = daemonIP;
        this.commandIP = commandIP;
        this.defaultBufferSize = defaultBufferSize;
        this.maxActiveThreads=maxActiveThreads;
        serverSocket.bind(this.daemonIP);
    }

    public void start() {
        command.bind(commandIP);
        active = true;
    }

    public void pause() {
        active = false;
    }

    public void unpause() {
        active = true;
    }

    public void recieve() {
        ThreadPoolExecutor executorService = Executors.newCachedThreadPool();
        ConcurrentHashMap < String, ByteBuffer > datamap = new ConcurrentHashMap < String, ByteBuffer > ();
        Runnable recieve = () -> {
            SocketChannel socket=socketQueue.pollFirst();
            final Thread currentThread = Thread.currentThread();
            Runnable priority = () -> {
                int counter;
                Thread t = Thread.currentThread();
                t.setPriority(1);
                while(true){
                    counter++;
                     if(counter==30){
                        currentThread.setPriority(7);
                    }
                    else if(counter==60){
                        currentThread.setPriority(10);
                        t.interrupt();
                        return;
                    }
                    t=Thread.sleep(1000);
                }
            };
            byte[] commandBytes;
            ByteBuffer buffer=ByteBuffer.allocate(defaultBufferSize);//change, just guesswork
            byte[] temp= new byte[1];
            temp[1]=0;
            buffer=ByteBuffer.wrap(temp);
            socket.write(buffer);
            buffer.clear();
            buffer.flip();
            int responseWait=0;
            currentThread.setPriority(1);
            while(responseWait<=timeOut){
                socket.read(buffer);
                if(buffer!=null){
                    buffer.clear();
                    break;
                }
                else if(responseWait<timeOut&&buffer==null){
                    responseWait++;
                    Thread.sleep(1);
                }
                else{
                    return;
                }
            }
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(priority);
            buffer.get(command, 0, 75);
            String[] commandComponents = command.toString().split(":");// 0 is command,1 is name, 2 is tier, 3 is authkey
            if (commandComponents[0].equals("get")) {
                buffer.clear();
                if (Integer.toHexString(components[2].hashCode()) == authCodes.get(components[0])) {
                    Object record=get(commandCompontents[1]);
                    record.
                    buffer.flip();
                    InetSocketAddress remote = new InetSocketAddress(InetAddress.getByName(components[2]));
                    socket.bind(daemonIP);
                    socket.connect(remote);
                    socket.finishConnect();
                    socket.write(buffer);
                } else {
                    return;
                }
            } else if (commandComponents[0].equals("set")) {
                authCodes.add(Integer.toHexString(components[2].hashCode()));
                InetSocketAddress remote = new InetSocketAddress(InetAddress.getByName(components[3]));
                /****************************************************************************/
                //This section verifies whether the recieved data is the data associated with the right sender
                /* byte[] data;
                buffer.get(data, 0, 1);
                Byte bt=data[0];
                if( bt.toString()!=token){
                    datamap.put(bt.toString(), buffer);
                    buffer.clear();
                    buffer.put(datamap.get(token));
                }
                byte[] bytes=buffer.array(); */
                buffer.position(75);
                buffer=buffer.slice();
                byte[] byteArray=buffer.array();
                /****************************************************************************/
                InputStream stream = new ByteArrayInputStream(byteArray);
                ObjectMetadata metadata = new ObjectMetatada();
                metadata.setContentType("text/plain");
                metadata.setContentLength(fileBytes.length);
                write(bucketName, components[1], stream, metadata);
            } else if (commands.contains("remove")) {
                if (Integer.toHexString(components[1].hashCode()) == authCodes.get(components[0])) {
                    remove(name);
                } else {
                    return;
                }
            }
        };
        while (active) {
            SocketChannel socket = serverSocket.accept();
            if (socket != null) {
                socketQueue.add(socket);
            }
            if(socketQueue.length!=0&&executorService.getActiveCount()<maxActiveThreads){
                executorService.execute(recieve);
            }
            else{
                continue;
            }
        }
    }

    public void remove(String name) {
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            s3client.deleteObject(new DeleteObjectRequest(bucketName, params[0].toString()));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
                    + "to Amazon S3, but was rejected with an error response" + " for some reason.");
            System.out.println("Error Message:" + ase.getMessage());
            System.out.println("HTTP Status Code:" + ase.getStatusCode());
            System.out.println("AWS Error Code:" + ase.getErrorCode());
            System.out.println("Error Type:" + ase.getErrorType());
            System.out.println("Request ID:" + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
                    + "an internal error while trying to " + "communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

    }

    public Object get(String file) {
        AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, file));
            InputStream objectData = object.getObjectContent();
            byte[] bytes = IOUtils.toByteArray(is);
            ByteBuffer b = ByteBuffer.wrap(bytes);
            return b;
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
                    + "to Amazon S3, but was rejected with an error response" + " for some reason.");
            System.out.println("Error Message:" + ase.getMessage());
            System.out.println("HTTP Status Code:" + ase.getStatusCode());
            System.out.println("AWS Error Code:" + ase.getErrorCode());
            System.out.println("Error Type:" + ase.getErrorType());
            System.out.println("Request ID:" + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
                    + "an internal error while trying to " + "communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }

    }

    public void write(String fileKey, InputStream stream, ObjectMetadata metadata) {
        String key = fileKey;
        AmazonS3 s3client = new AmazonS3Client(new ProfileCredentialsProvider());
        try {
            s3client.putObject(new PutObjectRequest(bucketName, key, stream, metadata));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which " + "means your request made it "
                    + "to Amazon S3, but was rejected with an error response" + " for some reason.");
            System.out.println("Error Message:" + ase.getMessage());
            System.out.println("HTTP Status Code:" + ase.getStatusCode());
            System.out.println("AWS Error Code:" + ase.getErrorCode());
            System.out.println("Error Type:" + ase.getErrorType());
            System.out.println("Request ID:" + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which " + "means the client encountered "
                    + "an internal error while trying to " + "communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
}