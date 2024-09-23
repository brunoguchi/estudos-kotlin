package com.exemplo

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusMessage
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.HttpMethod
import com.microsoft.azure.functions.HttpRequestMessage
import com.microsoft.azure.functions.HttpResponseMessage
import com.microsoft.azure.functions.HttpStatus
import com.microsoft.azure.functions.annotation.AuthorizationLevel
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.HttpTrigger
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger
import java.util.*

class FunctionsHandler {

    @FunctionName("HttpTrigger-Kotlin")
    fun run(
        @HttpTrigger(
            name = "req",
            methods = [HttpMethod.POST],
            authLevel = AuthorizationLevel.FUNCTION
        ) request: HttpRequestMessage<Optional<String>>,
        context: ExecutionContext
    ): HttpResponseMessage {

        context.logger.info("HTTP trigger processed a ${request.httpMethod.name} request.")

        val query = request.queryParameters["name"]
        val name = request.body.orElse(query)

        name?.let {
            return request
                .createResponseBuilder(HttpStatus.OK)
                .body("Hello, $name!")
                .build()
        }

        return request
            .createResponseBuilder(HttpStatus.BAD_REQUEST)
            .body("Please pass a name on the query string or in the request body")
            .build()
    }

    @FunctionName("QueueTrigger-Kotlin")
    fun runQueueTrigger(
        @ServiceBusQueueTrigger(
            name = "message",
            queueName = "test-db1-noguchi-corequeueupdatestock",
            connection = "AzureWebJobsStorage"
        ) message: String,
        context: ExecutionContext
    ) {
        context.logger.info("Java Service Bus Queue trigger function executed.")
        context.logger.info(message)
        sendMessageToQueue(message, context)
    }

    fun sendMessageToQueue(values: String, context: ExecutionContext) {
        val connectionString = System.getenv("AzureWebJobsStorage")

        val sender = ServiceBusClientBuilder()
            .connectionString(connectionString)
            .sender()
            .queueName("test-db1-noguchi-marketplaceupdatestockqueue")
            .buildClient()

        val message = ServiceBusMessage(values);
        sender.sendMessage(message)
        sender.close()

        context.logger.info("Message sent to marketplace queue.")
    }
}
