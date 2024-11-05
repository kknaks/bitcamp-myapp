package bitcamp.myapp.service;

import java.util.*;
import java.util.stream.Collectors;

public class RouteService {
  // 관광지들을 가장 가까운 숙소에 할당
  public static Map<Integer, List<Assignment>> assignAttractionsToHotels(
      double[][] distances,
      List<Integer> hotels,
      List<Integer> attractions) {

    Map<Integer, List<Assignment>> hotelAssignments = new HashMap<>();
    for (int hotel : hotels) {
      hotelAssignments.put(hotel, new ArrayList<>());
    }

    // 각 관광지에 대해 가장 가까운 숙소 찾기
    for (int attraction : attractions) {
      double minDistance = Double.POSITIVE_INFINITY;
      int bestHotel = -1;

      for (int hotel : hotels) {
        double distance = distances[hotel][attraction];
        if (distance < minDistance) {
          minDistance = distance;
          bestHotel = hotel;
        }
      }

      // 찾은 숙소에 관광지 할당
      hotelAssignments.get(bestHotel).add(new Assignment(attraction, minDistance));
    }

    return hotelAssignments;
  }

  // 한 숙소에 할당된 관광지들의 최적 방문 순서 찾기
  public static Route findDailyRoute(int hotel, List<Assignment> assignedAttractions,
      double[][] distances) {
    List<Integer> route = new ArrayList<>();
    route.add(hotel);
    double totalDistance = 0;

    List<Assignment> remaining = new ArrayList<>(assignedAttractions);
    int currentPos = hotel;

    while (!remaining.isEmpty()) {
      double minDistance = Double.POSITIVE_INFINITY;
      int nextStop = -1;
      int nextIdx = 0;

      // 현재 위치에서 가장 가까운 다음 관광지 찾기
      for (int i = 0; i < remaining.size(); i++) {
        double distance = distances[currentPos][remaining.get(i).attraction];
        if (distance < minDistance) {
          minDistance = distance;
          nextStop = remaining.get(i).attraction;
          nextIdx = i;
        }
      }

      route.add(nextStop);
      totalDistance += minDistance;
      currentPos = nextStop;
      remaining.remove(nextIdx);
    }

    // 마지막 관광지에서 숙소로 돌아오기
    route.add(hotel);
    totalDistance += distances[currentPos][hotel];

    return new Route(route, totalDistance);
  }

  public static Map<Integer, HotelPlan> planOptimalTrip(
      double[][] distances,
      List<Integer> hotels,
      List<Integer> attractions) {

    // 관광지들을 가장 가까운 숙소에 할당
    Map<Integer, List<Assignment>> hotelAssignments =
        assignAttractionsToHotels(distances, hotels, attractions);

    // 각 숙소별로 최적 경로 계산
    Map<Integer, HotelPlan> tripPlan = new HashMap<>();

    for (Map.Entry<Integer, List<Assignment>> entry : hotelAssignments.entrySet()) {
      int hotel = entry.getKey();
      List<Assignment> assigned = entry.getValue();

      if (!assigned.isEmpty()) {
        Route route = findDailyRoute(hotel, assigned, distances);
        List<Integer> assignedAttractions = assigned.stream()
            .map(a -> a.attraction)
            .collect(Collectors.toList());

        tripPlan.put(hotel, new HotelPlan(
            assignedAttractions,
            route.path,
            route.totalDistance
        ));
      }
    }

    return tripPlan;
  }

  public static void main(String[] args) {
    // 예시 데이터
    double[][] distances = {
        {0, 20, 30, 10, 50, 30, 40, 20},  // 숙소0
        {20, 0, 25, 40, 10, 20, 30, 35},  // 숙소1
        {30, 25, 0, 35, 40, 15, 10, 45},  // 숙소2
        {10, 40, 35, 0, 25, 20, 30, 15},  // 관광1
        {50, 10, 40, 25, 0, 30, 35, 40},  // 관광2
        {30, 20, 15, 20, 30, 0, 20, 25},  // 관광3
        {40, 30, 10, 30, 35, 20, 0, 30},  // 관광4
        {20, 35, 45, 15, 40, 25, 30, 0}   // 관광5
    };

    List<Integer> hotels = Arrays.asList(0, 1, 2);
    List<Integer> attractions = Arrays.asList(3, 4, 5, 6, 7);

    Map<Integer, HotelPlan> plan = planOptimalTrip(distances, hotels, attractions);

    // 결과 출력
    System.out.println("=== 여행 계획 ===");
    for (Map.Entry<Integer, HotelPlan> entry : plan.entrySet()) {
      System.out.println("\n숙소 " + entry.getKey() + ":");
      System.out.println("할당된 관광지: " + entry.getValue().attractions);
      System.out.println("방문 순서: " + entry.getValue().route);
      System.out.println("총 이동 거리: " + entry.getValue().totalDistance);
    }
  }


  static class Assignment {
    int attraction;
    double distance;

    Assignment(int attraction, double distance) {
      this.attraction = attraction;
      this.distance = distance;
    }
  }


  static class Route {
    List<Integer> path;
    double totalDistance;

    Route(List<Integer> path, double totalDistance) {
      this.path = path;
      this.totalDistance = totalDistance;
    }
  }


  static class HotelPlan {
    List<Integer> attractions;
    List<Integer> route;
    double totalDistance;

    HotelPlan(List<Integer> attractions, List<Integer> route, double totalDistance) {
      this.attractions = attractions;
      this.route = route;
      this.totalDistance = totalDistance;
    }
  }
}
