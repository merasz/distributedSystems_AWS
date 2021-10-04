package homework1;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.*;
import com.amazonaws.services.ec2.model.RunInstancesResult;

import java.util.Collections;
import java.util.List;

public class TaskOne {

	public static void main(String[] args) {
		AWSCredentials credentials = new ProfileCredentialsProvider().getCredentials();

		AmazonEC2 ec2 = AmazonEC2ClientBuilder
				.standard()
				.withCredentials(new AWSStaticCredentialsProvider(credentials))
				.withRegion(Regions.US_EAST_1)
				.build();

		String securityGroupName = "rundschreibena5881";
		String keyName = "ichbineinwildschwein91";

//		https://docs.aws.amazon.com/de_de/sdk-for-java/v1/developer-guide/create-key-pair.html

		CreateKeyPairRequest createKeyPairRequest = new CreateKeyPairRequest();
		createKeyPairRequest.withKeyName(keyName);
		CreateKeyPairResult createKeyPairResult = ec2.createKeyPair(createKeyPairRequest);
		KeyPair keyPair = new KeyPair();
		keyPair = createKeyPairResult.getKeyPair();
		String privateKey = keyPair.getKeyMaterial();

//		https://docs.aws.amazon.com/de_de/sdk-for-java/v1/developer-guide/create-security-group.html
		CreateSecurityGroupRequest csgr = new CreateSecurityGroupRequest();
		csgr.withGroupName(securityGroupName).withDescription("My security group neu");
		CreateSecurityGroupResult createSecurityGroupResult = ec2.createSecurityGroup(csgr);

		String ipAddr = "0.0.0.0/0";
		List<String> ipRanges = Collections.singletonList(ipAddr);
		IpPermission ipPermission = new IpPermission();
		ipPermission
				.withIpProtocol("tcp")
				.withFromPort(22)
				.withToPort(22)
				.withIpRanges(ipRanges);
		List<IpPermission> ipPermissions = Collections.singletonList(ipPermission);
		AuthorizeSecurityGroupIngressRequest ingressRequest = new AuthorizeSecurityGroupIngressRequest(securityGroupName, ipPermissions);
		ec2.authorizeSecurityGroupIngress(ingressRequest);


//		https://docs.aws.amazon.com/de_de/sdk-for-java/v1/developer-guide/run-instance.html
		RunInstancesRequest runInstancesRequest = new RunInstancesRequest()
				.withImageId("ami-0dba2cb6798deb6d8")
				.withInstanceType(InstanceType.T2Micro)
				.withMinCount(1)
				.withMaxCount(1)
				.withKeyName(keyName)
				.withSecurityGroups(securityGroupName);

		RunInstancesResult runInstancesResult = ec2.runInstances(runInstancesRequest);

		long start, end;
		start = System.currentTimeMillis();

		String securityGroupId = createSecurityGroupResult.getGroupId();
		String instanceId = getInstanceId(ec2, runInstancesResult);
		System.out.println("Instance ID: " + instanceId);

		Integer instanceState = -1;
		while(instanceState != 16) {
			instanceState = getInstanceStatus(ec2, instanceId);
		}
		end = System.currentTimeMillis();
		System.out.println("Benoetigte Zeit zum start der Instance: " + (end - start)/1000 + " Sekunden.");

		System.out.println("Instance beenden ...");
		terminateInstance(ec2, instanceId);
		System.out.println("Instance beendet!");

/*
		instanceState = -1;
		while(instanceState != 48) {
			instanceState = getInstanceStatus(ec2, instanceId);
		}

		deleteSecurityGroup(ec2, securityGroupId);

		DeleteKeyPairRequest request = new DeleteKeyPairRequest()
				.withKeyName(keyName);
		DeleteKeyPairResult response = ec2.deleteKeyPair(request);*/

	}

	static public String getInstanceId(AmazonEC2 ec2, RunInstancesResult runInstancesResult) {
		String instanceId = runInstancesResult.getReservation().getInstances().get(0).getInstanceId();
		return instanceId;
	}

	/*		0 : pending
    16 : running
    32 : shutting-down
    48 : terminated
    64 : stopping
    80 : stopped*/
	//https://stackoverflow.com/questions/25965268/how-can-i-check-instance-state-when-i-first-launch-it
	static public Integer getInstanceStatus(AmazonEC2 ec2, String instanceId) {
		DescribeInstancesRequest describeInstanceRequest = new DescribeInstancesRequest().withInstanceIds(instanceId);
		DescribeInstancesResult describeInstanceResult = ec2.describeInstances(describeInstanceRequest);
		InstanceState state = describeInstanceResult.getReservations().get(0).getInstances().get(0).getState();
		return state.getCode();
	}

	static public void terminateInstance(AmazonEC2 ec2, String instanceId) {
		StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
		ec2.stopInstances(request);
	}

	public static void deleteSecurityGroup(AmazonEC2 ec2, String securityGroupName){
		DeleteSecurityGroupRequest deleteRequest = new DeleteSecurityGroupRequest()
				.withGroupId(securityGroupName);
		DeleteSecurityGroupResult deleteResponse = ec2.deleteSecurityGroup(deleteRequest);
	}

}