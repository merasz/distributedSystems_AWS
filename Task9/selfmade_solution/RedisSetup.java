package homework1;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.waiters.WaiterParameters;
import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

public class RedisSetup {

    private static final String REDIS_PASSWORD = "PeterPan";
    public static final String keyName = "pan";
    public static final String securityGroupName = "launch-wizard-1";
    public static final String securityGroupDescription = "task2_description";
    public static final String amiId = "ami-0be2609ba883822ec";
    public static final String keyPairPath = "/home/michael/Downloads/pan.pem";

    public static void main(String[] args) {
        int N = Integer.parseInt(args[1]);
        run();
    }

    public static String getInstanceId(AmazonEC2 ec2, RunInstancesResult runInstancesResult) {
        String instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
        return instanceId;
    }

    public static String waitForInstanceToStart(AmazonEC2 ec2, String instanceId) {
        DescribeInstancesRequest describeRequest = new DescribeInstancesRequest();
        describeRequest.setInstanceIds(Collections.singleton(instanceId));

        ec2.waiters().instanceRunning().run(new WaiterParameters<DescribeInstancesRequest>(describeRequest));
        DescribeInstancesResult result = ec2.describeInstances(describeRequest);
        Instance instance = result.getReservations().get(0).getInstances().get(0);
        return instance.getPublicIpAddress();
    }
    public static String getPrivateIp (AmazonEC2 ec2, String instanceId){
        DescribeInstancesRequest describeRequest = new DescribeInstancesRequest();
        describeRequest.setInstanceIds(Collections.singleton(instanceId));

        ec2.waiters().instanceRunning().run(new WaiterParameters<DescribeInstancesRequest>(describeRequest));
        DescribeInstancesResult result = ec2.describeInstances(describeRequest);
        Instance instance = result.getReservations().get(0).getInstances().get(0);
        return instance.getPrivateIpAddress();
    }
    public static String getPublicDNS (AmazonEC2 ec2, String instanceId){
        DescribeInstancesRequest describeRequest = new DescribeInstancesRequest();
        describeRequest.setInstanceIds(Collections.singleton(instanceId));

        ec2.waiters().instanceRunning().run(new WaiterParameters<DescribeInstancesRequest>(describeRequest));
        DescribeInstancesResult result = ec2.describeInstances(describeRequest);
        Instance instance = result.getReservations().get(0).getInstances().get(0);
        return instance.getPublicDnsName();
    }

    public static RunInstancesResult startInstance(AmazonEC2 ec2, String securityGroupName) {
        RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
                .withImageId(amiId)
                .withInstanceType(InstanceType.C4Xlarge)
                .withUserData(getUserDataNormalNodeNoSudo())
                .withMinCount(1)
                .withMaxCount(1)
                .withKeyName(keyName)
                .withSecurityGroups(securityGroupName);
        return ec2.runInstances(runInstancesRequest);
    }


    public static void run() {
        //load aws credentials
        AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();
        //create aws ec2 client
        AmazonEC2 ec2 = AmazonEC2ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();

        //run the ec2
        RunInstancesResult runInstancesResult = startInstance(ec2, securityGroupName);
        System.out.println("Trying to create instance!\n");

        long start, end;
        start = System.currentTimeMillis();

        String instanceId = getInstanceId(ec2, runInstancesResult);
        String instancePublicIP = waitForInstanceToStart(ec2, instanceId);
        String instancePrivateIP = getPrivateIp(ec2, instanceId);
        String publicDNS = getPublicDNS(ec2, instanceId);
        end = System.currentTimeMillis();

        System.out.println("Instance ID1:           " + instanceId + "  Instance PublicIP:  " + instancePublicIP);
        System.out.println("Instance public DNS:    " +publicDNS+ "     Instance PrivateIp: " + instancePrivateIP);
        System.out.println("time elapsed: " + (end - start) / 1000 + "second(s)");

        try {
            Thread.sleep(20000);
        } catch (Exception e) {
            System.err.println(e);
        }

    }
    private static String getUserDataNormalNodeNoSudo() {
        StringBuilder userData = new StringBuilder();
        userData.append("#!/bin/bash\n");
        userData.append("yum -y update\n");
        userData.append("sudo yum install -y gcc make tcl\n");
        userData.append("yum groupinstall \"Development Tools\"\n");
        userData.append("cd /usr/local/src\n");
        userData.append("wget https://download.redis.io/releases/redis-6.0.9.tar.gz\n");
        userData.append("tar xzf redis-6.0.9.tar.gz\n");
        userData.append("cd redis-6.0.9\n");
        userData.append("make\n");
        userData.append("cp src/redis-server /usr/local/bin/\n");
        userData.append("cp src/redis-cli /usr/local/bin/\n");
        userData.append("cp src/redis-benchmark /usr/local/bin/\n");
        userData.append("echo \"port 6379\nrequirepass " + REDIS_PASSWORD + "\" >> /home/ec2-user/redis-server.cfg\n");
        userData.append("chown ec2-user:ec2-user /home/ec2-user/redis-server.cfg\n");
        userData.append("chmod +x /home/ec2-user/redis-server.cfg\n");
        userData.append("/usr/local/bin/redis-server /home/ec2-user/redis-server.cfg\n");
        String userDataNew = encodeBase64(userData.toString());
        return userDataNew;
    }

    private static String encodeBase64(String input) {
        String base64UserData = null;
        try {
            base64UserData = new String(Base64.encodeBase64(input.getBytes("UTF-8")), "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            System.out.println(uee.getMessage());
        }
        return base64UserData;
    }
}