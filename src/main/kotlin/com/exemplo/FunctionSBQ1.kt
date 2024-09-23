package com.exemplo

import com.azure.messaging.servicebus.ServiceBusClientBuilder
import com.azure.messaging.servicebus.ServiceBusMessage
import com.microsoft.azure.functions.ExecutionContext
import com.microsoft.azure.functions.annotation.FunctionName
import com.microsoft.azure.functions.annotation.ServiceBusQueueTrigger

class FunctionSBQ1 {

    @FunctionName("QueueTrigger-Kotlin")
    fun run(
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
