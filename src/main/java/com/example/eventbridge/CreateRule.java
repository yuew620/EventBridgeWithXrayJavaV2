//snippet-sourcedescription:[CreateRule.java demonstrates how to create an Amazon EventBridge rule.]
//snippet-keyword:[AWS SDK for Java v2]
//snippet-service:[Amazon EventBridge]

/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

package com.example.eventbridge;

// snippet-start:[eventbridge.java2._create_rule.import]
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.eventbridge.EventBridgeClient;
import software.amazon.awssdk.services.eventbridge.model.PutRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.PutRuleResponse;
import software.amazon.awssdk.services.eventbridge.model.EventBridgeException;
// snippet-end:[eventbridge.java2._create_rule.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */
public class CreateRule {

    public static void main(String[] args) {

        final String usage = "\n" +
            "Usage:\n" +
            "    <ruleName> \n\n" +
            "Where:\n" +
            "    ruleName - The name of the rule to create. \n";

        if (args.length != 1) {
            System.out.println(usage);
            System.exit(1);
        }

        String ruleName = args[0];
        Region region = Region.US_WEST_2;
        EventBridgeClient eventBrClient = EventBridgeClient.builder()
            .region(region)
            .credentialsProvider(ProfileCredentialsProvider.create())
            .build();

        createEBRule(eventBrClient, ruleName);
        eventBrClient.close();
    }

    // snippet-start:[eventbridge.java2._create_rule.main]
    public static void createEBRule(EventBridgeClient eventBrClient, String ruleName) {

        try {
            PutRuleRequest ruleRequest = PutRuleRequest.builder()
                .name(ruleName)
                .eventBusName("default")
                .eventPattern("{\"source\":[\"aws.s3\"],\"detail-type\":[\"AWS API Call via CloudTrail\"],\"detail\":{\"eventSource\":[\"s3.amazonaws.com\"],\"eventName\":[\"DeleteBucket\"]}}")
                .description("A test rule created by the Java API")
                .build();

            PutRuleResponse ruleResponse = eventBrClient.putRule(ruleRequest);
            System.out.println("The ARN of the new rule is "+ ruleResponse.ruleArn());

        } catch (EventBridgeException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    // snippet-end:[eventbridge.java2._create_rule.main]
}
