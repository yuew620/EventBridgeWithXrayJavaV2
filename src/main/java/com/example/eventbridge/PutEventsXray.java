//snippet-sourcedescription:[PutEvents.java demonstrates how to send custom events to Amazon EventBridge.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon EventBridge]
/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._put_event.import]

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.*;

import java.util.ArrayList;
import java.util.List;
import com.amazonaws.xray.interceptors.TracingInterceptor;
// snippet-end:[eventbridge.java2._put_event.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class PutEventsXray {

    public static void main(String[] args) {

        final String usage =
            "To run this example, supply two resources, identified by Amazon Resource Name (ARN), which the event primarily concerns. " +
            "Any number, including zero, may be present. \n" +
            "For example: PutEvents <resourceArn> <resourceArn2>\n";

        if (args.length != 2) {
            System.out.println(usage);
            System.exit(1);
        }

        String resourceArn = args[0];
        String resourceArn2 = args[1];
        Region region = Region.US_EAST_1;

        //eventBridgeClient
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        //eventBridgeWithXray AsyncClient
        EventBridgeAsyncClient eventBrAscClient = EventBridgeAsyncClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                            // manual instrumentation as described here https://aws.amazon.com/blogs/developer/x-ray-support-for-the-aws-sdk-for-java-v2/
                            .addExecutionInterceptor(new TracingInterceptor())
                            .build())
                .build();

        //eventBridgeWithXrayClient
        EventBridgeClient eventBrClientWithXray = EventBridgeClient.builder()
                .region(region)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .overrideConfiguration(ClientOverrideConfiguration.builder()
                        // manual instrumentation as described here https://aws.amazon.com/blogs/developer/x-ray-support-for-the-aws-sdk-for-java-v2/
                        .addExecutionInterceptor(new TracingInterceptor())
                        .build())
                .build();


        putEBEvents(eventBrClientWithXray, resourceArn, resourceArn2);
        eventBrClient.close();
    }

    // snippet-start:[eventbridge.java2._put_event.main]
    public static void putEBEvents(EventBridgeClient eventBrClient, String resourceArn, String resourceArn2 ) {

        try {
            // Populate a List with the resource ARN values.
            List<String> resources = new ArrayList<>();
            resources.add(resourceArn);
            resources.add(resourceArn2);

            PutEventsRequestEntry reqEntry = PutEventsRequestEntry.builder()
                .resources(resources)
                .source("com.mycompany.myapp")
                .detailType("myDetailType")
                .detail("{ \"key1\": \"value1\", \"key2\": \"value2\" }")
                .build();

            PutEventsRequest eventsRequest = PutEventsRequest.builder()
                .entries(reqEntry)
                .build();

            PutEventsResponse result = eventBrClient.putEvents(eventsRequest);
            for (PutEventsResultEntry resultEntry : result.entries()) {
                if (resultEntry.eventId() != null) {
                    System.out.println("Event Id: " + resultEntry.eventId());
                } else {
                    System.out.println("Injection failed with Error Code: " + resultEntry.errorCode());
                }
            }

        } catch (EventBridgeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[eventbridge.java2._put_event.main]
}
