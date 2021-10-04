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
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


import java.util.Collections;
import java.util.List;

/*
* task 2,3 sind das framework fuer 4
* task4 siehe source code
*
* task5
* die berechnung aller 47 fibo zahlen benoetigt in etwa 300 sekunden
* mit 2 vms koennte man die zeit halbieren, da die berechnung unter den threads aufgeteilt wird
* mittels erstellen eines sets von gerade und ungeraden kann man beinahe den speedup von 2 erreichen
* wir haben uns hierbei dafuer entschieden den input in 2 teile zu teilen, der eine thread errechnet die werte bis 45
* und der andere die werte von 45 bis 47, und es exponentiel waechst wird trotz aller anstrengung der 2te thread laenger rechnen
*
* gruppe, markus koerberle, nils rambacher, michael hauser
* */

public class TaskOne {

	public static void main(String[] args) {


		RunnableExample threadOne = new RunnableExample("input_half_one.csv", "01");
		RunnableExample threadTwo = new RunnableExample("input_half_two.csv", "02");

		threadOne.start();
		threadTwo.start();

	}
}