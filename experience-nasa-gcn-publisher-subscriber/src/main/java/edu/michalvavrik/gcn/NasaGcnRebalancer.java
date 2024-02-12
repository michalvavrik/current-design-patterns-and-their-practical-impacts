package edu.michalvavrik.gcn;

import io.smallrye.common.annotation.Identifier;
import io.smallrye.reactive.messaging.kafka.KafkaConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;

import jakarta.enterprise.context.ApplicationScoped;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * adjusted c&p from https://quarkus.io/guides/kafka#consumer-rebalance-listener
 */
@ApplicationScoped
@Identifier("nasa-gcn-rebalancer")
public class NasaGcnRebalancer implements KafkaConsumerRebalanceListener {

    private static final Logger LOGGER = Logger.getLogger(NasaGcnRebalancer.class.getName());

    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        long now = System.currentTimeMillis();
        long shouldStartAt = now - Duration.ofDays(1).toMillis();

        Map<TopicPartition, Long> request = new HashMap<>();
        for (TopicPartition partition : partitions) {
            LOGGER.info("Assigned " + partition);
            request.put(partition, shouldStartAt);
        }
        Map<TopicPartition, OffsetAndTimestamp> offsets = consumer.offsetsForTimes(request);
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> position : offsets.entrySet()) {
            long target = position.getValue() == null ? 0L : position.getValue().offset();
            LOGGER.info("Seeking position " + target + " for " + position.getKey());
            consumer.seek(position.getKey(), target);
        }
    }

}