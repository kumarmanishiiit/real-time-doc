package org.example.sec06;

import com.assignment.knn.model.DataPoint;
import com.assignment.knn.model.KNNRequest;
import com.assignment.knn.model.KNNResponse;
import com.assignment.knn.model.KNNServiceGrpc;
import com.iiith.assignment.model.sec06.AccountBalance;
import com.iiith.assignment.model.sec06.BalanceCheckRequest;
import com.iiith.assignment.model.sec06.BankServiceGrpc;
import io.grpc.stub.StreamObserver;
import org.example.repository.AccountRepository;
import org.example.repository.DataPointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNNService extends KNNServiceGrpc.KNNServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(KNNService.class);

    @Override
    public void findKNearestNeighbors(KNNRequest request, StreamObserver<KNNResponse> responseObserver) {
//        log.info("request received {}", request.getDataPoint());
        var dataPoint = request.getDataPoint();
        var x_cord = dataPoint.getXCord();
        var y_cord = dataPoint.getYCord();
        var k = request.getK();

        Map<Float, DataPoint> dataPointList = new HashMap<>();

        Map<Float, Float> allData = DataPointRepository.getDataPoint();

        // Iterating using enhanced for loop
        for (Map.Entry<Float, Float> entry : allData.entrySet()) {
            Float key = entry.getKey();
            Float value = entry.getValue();
            dataPointList.put(calculateDistance(key, value, x_cord, y_cord), DataPoint.newBuilder().setXCord(key).setYCord(value).build());
        }

        // Sort the map by keys and extract k elements
        List<com.assignment.knn.model.DataPointResponse> sortedAndExtracted = sortAndExtract(dataPointList, k);


//        DataPoint dataPoint1 = DataPoint.newBuilder().setXCord(1.2f).setYCord(3.4f).build();

        var kDataPoint = KNNResponse.newBuilder().addAllKDataPoint(sortedAndExtracted).build();
        responseObserver.onNext(kDataPoint);
        responseObserver.onCompleted();
    }

    public static List<com.assignment.knn.model.DataPointResponse> sortAndExtract(Map<Float, DataPoint> dataPointList, int k) {
        // Step 1: Sort the map by keys
        List<Map.Entry<Float, DataPoint>> entryList = new ArrayList<>(dataPointList.entrySet());

        // Sort based on the key (Float)
        entryList.sort(Map.Entry.comparingByKey());

        // Step 2: Extract the first 'k' elements
        List<com.assignment.knn.model.DataPointResponse> result = new ArrayList<>();
        for (int i = 0; i < k && i < entryList.size(); i++) {
//            DataPoint.newBuilder().setXCord().setYCord().build()
            result.add(com.assignment.knn.model.DataPointResponse.newBuilder().setDataPoint(entryList.get(i).getValue()).setDistance(entryList.get(i).getKey()).build());
        }

        return result;
    }

    public static Float calculateDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    @Override
    public void populateData(DataPoint request, StreamObserver<com.assignment.knn.model.Output> responseObserver) {
//        log.info("request received  Manish {}", request.getXCord());

        DataPointRepository.getDataPoint().put(request.getXCord(), request.getYCord());

//        log.info("Map values now {}", DataPointRepository.getDataPoint());

        responseObserver.onNext(com.assignment.knn.model.Output.newBuilder().setValue(true).build());
        responseObserver.onCompleted();
    }
}
