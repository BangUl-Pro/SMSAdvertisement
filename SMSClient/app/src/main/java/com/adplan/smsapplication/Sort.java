package com.adplan.smsapplication;

import com.adplan.smsapplication.entities.UserEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IronFactory on 2016. 2. 10..
 */
public class Sort {

    // 오름차순
    public static final int TYPE_DESC = 1;

    // 내림차순
    public static final int TYPE_ASC = 2;


    public static final List<UserEntity> quickSort(List<UserEntity> list, int type) {
        if (list.size() < 2)
            return list;

        List<Boolean> finishList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            finishList.add(false);
        }

        int pivotIndex;
        int lowIndex;
        int highIndex;
        int temp;
        UserEntity tempObject;

        while ((pivotIndex = checkSort(finishList)) != -1) {
            lowIndex = pivotIndex + 1;
            highIndex = getHighIndex(finishList, pivotIndex);

            // 오름차순
            if (type == TYPE_DESC) {
                // low가 피벗보다 작거나 같으면 넘어간다
                if (list.get(lowIndex).getCoin() <= list.get(pivotIndex).getCoin()) {
                    lowIndex++;
                    continue;
                }
                // high가 피벗보다 크거나 같으면 넘어간다
                else if (list.get(highIndex).getCoin() >= list.get(pivotIndex).getCoin()) {
                    highIndex--;
                    continue;
                }
            }
            // 내림차순
            else if (type == TYPE_ASC) {
                // low가 피벗보다 크거나 같으면 넘어간다
                if (list.get(lowIndex).getCoin() >= list.get(pivotIndex).getCoin()) {
                    lowIndex++;
                    continue;
                }
                // high가 피벗보다 작거나 같으면 넘어간다
                else if (list.get(highIndex).getCoin() <= list.get(pivotIndex).getCoin()) {
                    highIndex--;
                    continue;
                }
            }

            // low가 high보다 작다면 둘이 바꾼다
            if (lowIndex < highIndex) {
                tempObject = list.get(lowIndex);
                list.set(lowIndex, list.get(highIndex));
                list.set(highIndex, tempObject);
            }
            // low가 high보다 크다면 high와 피벗을 바꾼다
            else if (highIndex < lowIndex) {
                tempObject = list.get(highIndex);
                list.set(highIndex, list.get(pivotIndex));
                list.set(pivotIndex, tempObject);

                finishList.set(pivotIndex, true);
            }
        }
        return list;
    }


    // 퀵정렬
    // 모든 데이터가 이미 정렬되어 있을 때 가장 느림
    public static final List<Object> quickSort(List<Object> list, List<Integer> orderList, int type) {
        List<Boolean> finishList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            finishList.add(false);
        }

        int pivotIndex;
        int lowIndex;
        int highIndex;
        int temp;
        Object tempObject;

        while ((pivotIndex = checkSort(finishList)) != -1) {
            lowIndex = pivotIndex + 1;
            highIndex = getHighIndex(finishList, pivotIndex);

            // 오름차순
            if (type == TYPE_DESC) {
                // low가 피벗보다 작거나 같으면 넘어간다
                if (orderList.get(lowIndex) <= orderList.get(pivotIndex)) {
                    lowIndex++;
                    continue;
                }
                // high가 피벗보다 크거나 같으면 넘어간다
                else if (orderList.get(highIndex) >= orderList.get(pivotIndex)) {
                    highIndex--;
                    continue;
                }
            }
            // 내림차순
            else if (type == TYPE_ASC) {
                // low가 피벗보다 크거나 같으면 넘어간다
                if (orderList.get(lowIndex) >= orderList.get(pivotIndex)) {
                    lowIndex++;
                    continue;
                }
                // high가 피벗보다 작거나 같으면 넘어간다
                else if (orderList.get(highIndex) <= orderList.get(pivotIndex)) {
                    highIndex--;
                    continue;
                }
            }

            // low가 high보다 작다면 둘이 바꾼다
            if (lowIndex < highIndex) {
                temp = orderList.get(lowIndex);
                orderList.set(lowIndex, orderList.get(highIndex));
                orderList.set(highIndex, temp);

                tempObject = list.get(lowIndex);
                list.set(lowIndex, list.get(highIndex));
                list.set(highIndex, tempObject);
            }
            // low가 high보다 크다면 high와 피벗을 바꾼다
            else if (highIndex < lowIndex) {
                temp = highIndex;
                orderList.set(highIndex, orderList.get(pivotIndex));
                orderList.set(pivotIndex, temp);

                tempObject = list.get(highIndex);
                list.set(highIndex, list.get(pivotIndex));
                list.set(pivotIndex, tempObject);

                finishList.set(pivotIndex, true);
            }
        }
        return list;
    }

    //인터페이스를 상속받은 객체 넘겨받기
//    public static final List<? extends SortType> quickSort(List<? extends SortTypes> list, int type) {
//        List<Boolean> finishList = new ArrayList<>();
//        for (int i = 0; i < list.size(); i++) {
//            finishList.add(false);
//        }
//
//        int pivotIndex;
//        int lowIndex;
//        int highIndex;
//        int temp;
//        Object tempObject;
//
//        while ((pivotIndex = checkSort(finishList)) != -1) {
//            lowIndex = pivotIndex + 1;
//            highIndex = getHighIndex(finishList, pivotIndex);
//
//            // 오름차순
//            if (type == TYPE_DESC) {
//                // low가 피벗보다 작거나 같으면 넘어간다
//                if (list.get(lowIndex).sortValue() <= list.get(pivotIndex).sortValue()) {
//                    lowIndex++;
//                    continue;
//                }
//                // high가 피벗보다 크거나 같으면 넘어간다
//                else if (list.get(highIndex).sortValue() >= list.get(pivotIndex).sortValue()) {
//                    highIndex--;
//                    continue;
//                }
//            }
//            // 내림차순
//            else if (type == TYPE_ASC) {
//                // low가 피벗보다 크거나 같으면 넘어간다
//                if (list.get(lowIndex).sortValue() >= list.get(pivotIndex).sortValue()) {
//                    lowIndex++;
//                    continue;
//                }
//                // high가 피벗보다 작거나 같으면 넘어간다
//                else if (list.get(highIndex).sortValue() <= list.get(pivotIndex).sortValue()) {
//                    highIndex--;
//                    continue;
//                }
//            }
//
//            // low가 high보다 작다면 둘이 바꾼다
//            if (lowIndex < highIndex) {
//                tempObject = list.get(lowIndex);
//                list.set(lowIndex, list.get(highIndex));
//                list.set(highIndex, tempObject);
//            }
//            // low가 high보다 크다면 high와 피벗을 바꾼다
//            else if (highIndex < lowIndex) {
//                temp = highIndex;
//                orderList.set(highIndex, orderList.get(pivotIndex));
//                orderList.set(pivotIndex, temp);
//
//                tempObject = list.get(highIndex);
//                list.set(highIndex, list.get(pivotIndex));
//                list.set(pivotIndex, tempObject);
//
//                finishList.set(pivotIndex, true);
//            }
//        }
//        return list;
//    }

//    public static final List<Object> quickSort(HashMap<Integer, Object> list, int type) {
//        List<Boolean> finishList = new List<>();
//        for (int i = 0; i < list.size(); i++) {
//            finishList.add(false);
//        }
//
//        int pivotIndex;
//        int lowIndex;
//        int highIndex;
//        int temp;
//        Object tempObject;
//
//        while ((pivotIndex = checkSort(finishList)) != -1) {
//            lowIndex = pivotIndex + 1;
//            highIndex = getHighIndex(finishList, pivotIndex);
//
//            int low = (int) list.keySet().toArray()[lowIndex];
//            int pivot = (int) list.keySet().toArray()[pivotIndex];
//            int high = (int) list.keySet().toArray()[highIndex];
//
//            // 오름차순
//            if (type == TYPE_UP) {
//                // low가 피벗보다 작거나 같으면 넘어간다
//                if (low <= pivot) {
//                    lowIndex++;
//                    continue;
//                }
//                // high가 피벗보다 크거나 같으면 넘어간다
//                else if (high >= pivot) {
//                    highIndex--;
//                    continue;
//                }
//            }
//            // 내림차순
//            else if (type == TYPE_DOWN) {
//                // low가 피벗보다 크거나 같으면 넘어간다
//                if (low >= pivot) {
//                    lowIndex++;
//                    continue;
//                }
//                // high가 피벗보다 작거나 같으면 넘어간다
//                else if (high <= pivot) {
//                    highIndex--;
//                    continue;
//                }
//            }
//
//            // low가 high보다 작다면 둘이 바꾼다
//            if (lowIndex < highIndex) {
//                tempObject = list.get(lowIndex);
//                list.
//                list.set(lowIndex, list.get(highIndex));
//                list.set(highIndex, tempObject);
//            }
//            // low가 high보다 크다면 high와 피벗을 바꾼다
//            else if (highIndex < lowIndex) {
//                temp = highIndex;
//                orderList.set(highIndex, orderList.get(pivotIndex));
//                orderList.set(pivotIndex, temp);
//
//                tempObject = list.get(highIndex);
//                list.set(highIndex, list.get(pivotIndex));
//                list.set(pivotIndex, tempObject);
//
//                finishList.set(pivotIndex, true);
//            }
//        }
//        return list;
//    }

    private static int checkSort(List<Boolean> list) {
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i))
                return i;
        }
        return -1;
    }

    private static int getHighIndex(List<Boolean> list, int pivot) {
        for (int i = pivot + 1; i < list.size(); i++) {
            if (list.get(i)) {
                return i - 1;
            }
        }
        return -1;
    }

    public interface SortType {
        int sortValue();
    }

    public static abstract class SortTypes {
        public abstract int sortValue();
    }
}
