package org.example.knn.client;

import java.util.*;
import java.util.concurrent.CountDownLatch;

public class KnnClient {

    public static void main(String[] args) throws InterruptedException {
        Map<Float, Float> data = Map.of(0.12f, 34.0f, 42.0f, 1.24f, 5.0f, -78.4f, 21.72f, -18.76f, 61.45f, 74.9f);

        // Now send these datasets to different servers
        sendDataToServers(data);
    }

    // Method to send data to the gRPC servers
    public static void sendDataToServers(Map<Float, Float> data) throws InterruptedException {
        GrpcClient client1 = new GrpcClient("localhost", 6565); // Server 1
        GrpcClient client2 = new GrpcClient("localhost", 7575); // Server 2

        CountDownLatch latch = new CountDownLatch(2);

        client1.knnQuery(latch, 21.41f, 34.34f);
        client2.knnQuery(latch, 21.41f, 34.34f);

        latch.await();

        com.assignment.knn.model.KNNResponse knnResponse1 = client1.knnResponses;
        com.assignment.knn.model.KNNResponse knnResponse2 = client2.knnResponses;

        List<com.assignment.knn.model.DataPointResponse> dataPointResponse = new ArrayList<>();
        dataPointResponse.addAll(knnResponse1.getKDataPointList());
        dataPointResponse.addAll(knnResponse2.getKDataPointList());

        PriorityQueue<com.assignment.knn.model.DataPointResponse> maxHeap = new PriorityQueue<>(2, new Comparator<com.assignment.knn.model.DataPointResponse>() {
            @Override
            public int compare(com.assignment.knn.model.DataPointResponse o1, com.assignment.knn.model.DataPointResponse o2) {
                return Float.compare(o2.getDistance(), o1.getDistance());
            }
        });

        for (com.assignment.knn.model.DataPointResponse pointResponse : dataPointResponse) {
            maxHeap.add(pointResponse);  // Add each element to the heap
            // If heap size exceeds k, remove the smallest element
            if (maxHeap.size() > 2) {
                maxHeap.poll();  // Removes the root (smallest element in the heap)
            }
        }

        for (int i = 0; i < 2; i++) {
            System.out.println(Objects.requireNonNull(maxHeap.poll()).getDataPoint());
        }
    }


}

