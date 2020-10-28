package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.AssertionError;

/**
* A priority queue class implemented using a max heap.
* Priorities cannot be negative.
*
* @author Lia Chin-Purcell and Sarah Walling-Bell
* @version September 21, 2018
*
*/
public class PriorityQueue {

  private Map<Integer, Integer> location;
  private List<Pair<Integer, Integer>> heap;

  /**
  *  Constructs an empty priority queue
  */
  public PriorityQueue() {
    heap = new ArrayList<Pair<Integer, Integer>>();
    location = new HashMap<Integer, Integer>();
  }

  /**
  *  Insert a new element into the queue with the
  *  given priority.
  *
  * @param priority priority of element to be inserted
  * @param element element to be inserted
  *
  * <dt><b>Preconditions:</b><dd>
  * <ul>
  * <li> The element does not already appear in the priority queue.</li>
  * <li> The priority is non-negative.</li>
  * </ul>
  *
  */
  public void push(int priority, int element) throws AssertionError {
    //printHeap();
    if((priority >= 0) && !isPresent(element)) {
      //make a new pair with the given arguments and add it to the end of the heap
      //percolate up until the properties of the max heap are restored
      Pair newPair = new Pair<Integer, Integer>(priority, element);
      heap.add(newPair);
      location.put(element, heap.size()-1);
      percolateUpLeaf();
    }
    else{
      throw new AssertionError("Priority queue must not be empty and must not contain input element.");
    }
  }

  /**
  *  Remove the highest priority element
  *
  * <dt><b>Preconditions:</b><dd>
  * <ul>
  * <li> The priority queue is non-empty.</li>
  * </ul>
  *
  */
  public void pop() throws AssertionError {
    if(!isEmpty()) {
      //get elements of first and last pairs
      int elemLast = heap.get(heap.size() - 1).element;
      int elemRoot = heap.get(0).element;

      //swap pairs, remove last element in heap, and push root into its place
      swap(elemLast, elemRoot);
      Pair removed = heap.remove(heap.size() - 1);
      int remove = location.remove(elemRoot);
      if(heap.size() != 0){
        pushDownRoot();
      }
    }
    else {
      throw new AssertionError("Priority queue must not be empty.");
    }
  }

  /**
  *  Returns the highest priority in the queue
  *  @return highest priority value
  *
  * <dt><b>Preconditions:</b><dd>
  * <ul>
  * <li> The priority queue is non-empty.</li>
  * </ul>
  */
  public int topPriority() throws AssertionError {
    if(!isEmpty()) {
      return heap.get(0).priority;
    }
    throw new AssertionError("Priority queue must not be empty.");
  }

  /**
  *  Returns the element with the highest priority
  *  @return element with highest priority
  *
  * <dt><b>Preconditions:</b><dd>
  * <ul>
  * <li> The priority queue is non-empty.</li>
  * </ul>
  */
  public int topElement() throws AssertionError {

    if(!isEmpty()) {
      return heap.get(0).element;
    }
    throw new AssertionError("Priority queue must not be empty.");
  }

  /**
  *  Change the priority of an element already in the
  *  priority queue.
  *
  *  @param element element whose priority is to be changed
  *  @param newpriority the new priority
  *
  * <dt><b>Preconditions:</b><dd>
  * <ul>
  * <li> The element exists in the priority queue</li>
  * </ul>
  */
  public void changePriority(int element, int newpriority) throws AssertionError {
    //printHeap();
    //if this element exists
    if(isPresent(element)) {
      //create a pair with new priority
      //replace old pair with new pair and swap into place
      Pair newPair = new Pair<>(newpriority, element);
      Pair oldPair = heap.set(location.get(element), newPair);

      if (newpriority < (int)oldPair.priority) {
        percolateUp(location.get(element));
      }
      if (newpriority > (int)oldPair.priority) {
        pushDown(location.get(element));
      }
    }
    else{
      throw new AssertionError("Priority queue must contain the input element.");
    }
  }

  /**
  *  Gets the priority of the element
  *
  *  @param element the element whose priority is returned
  *  @return the priority value
  *
  * <dt><b>Preconditions:</b><dd>
  * <ul>
  * <li> The element exists in the priority queue</li>
  * </ul>
  */
  public int getPriority(int element) throws AssertionError {
    if(isPresent(element)) {
      return heap.get(location.get(element)).priority;
    }
    else{
      throw new AssertionError("Priority queue must contain the input element.");
    }
  }

  /**
  *  Returns true if the priority queue contains no elements
  *  @return true if the queue contains no elements, false otherwise
  */
  public boolean isEmpty() {
    return heap.isEmpty();
  }

  /**
  *  Returns true if the element exists in the priority queue.
  *  @return true if the element exists, false otherwise
  */
  public boolean isPresent(int element) {
    //check if the element is in the map
    if(!location.containsKey(element)){
      return false;
    }
    //the element is in the map, therefore it is in the queue
    return true;
  }

  /**
  *  Removes all elements from the priority queue
  */
  public void clear() {
    heap.clear();
  }

  /**
  *  Returns the number of elements in the priority queue
  *  @return number of elements in the priority queue
  */
  public int size() {
    return heap.size();
  }



  /*********************************************************
  *              Private helper methods
  *********************************************************/


  /**
  * Push down a given element
  * @param start_index the index of the element to be pushed down
  * @return the index in the list where the element is finally stored
  */
  private int pushDown(int start_index) {
    //get the element and its priority
    int elemI = heap.get(start_index).element;
    int priorI = heap.get(start_index).priority;
    //System.out.println(elemI);
    //System.out.println(priorI);
    //push until the pair is in the correct place
    boolean inPlace = false;
    while (!inPlace && !isLeaf(location.get(elemI))) {
      //get the left child
      int priorLeft = heap.get(left(location.get(elemI))).priority;
      int priorBigChild;
      int elemBigChild;

      //only if the element as two children, get the right child and set the biggest child
      if (hasTwoChildren(location.get(elemI))) {
        int priorRight = heap.get(right(location.get(elemI))).priority;
        if (priorLeft <= priorRight) {
          priorBigChild = priorLeft;
          elemBigChild = heap.get(left(location.get(elemI))).element;
        }
        else {
          priorBigChild = priorRight;
          elemBigChild = heap.get(right(location.get(elemI))).element;
        }
      }

      else {
        priorBigChild = priorLeft;
        elemBigChild = heap.get(left(location.get(elemI))).element;
      }
      //if biggest child < element, swap
      if (priorBigChild < priorI) {
        swap(elemI, elemBigChild);
      }
      else {
        inPlace = true;
      }
    }
    return location.get(elemI);
  }

  /**
  * Percolate up a given element
  * @param start_index the element to be percolated up
  * @return the index in the list where the element is finally stored
  */
  private int percolateUp(int start_index) {
    //get the element and its priority
    int elemI = heap.get(start_index).element;
    int priorI = heap.get(start_index).priority;
    boolean inPlace = false;

    while (!inPlace) {
      //get larger of the element's left and right children
      int priorParent = heap.get(parent(location.get(elemI))).priority;
      int elemParent = heap.get(parent(location.get(elemI))).element;

      //if priorParent < element, swap
      if (priorParent > priorI) {
        swap(elemI, elemParent);

      }
      else {
        inPlace = true;
      }
    }
    return location.get(elemI);
  }


  /**
  * Swaps two elements in the priority queue by updating BOTH
  * the list representing the heap AND the map
  * @param i element to be swapped
  * @param j element to be swapped
  */
  private void swap(int i, int j) {
    //hash to find location of elements i and j
    int localI = location.get(i);
    int localJ = location.get(j);

    //swap elements i and j in list
    Pair pairTemp = heap.set(localJ, heap.get(localI));
    pairTemp = heap.set(localI, pairTemp);
    //swap elements in map
    boolean swapped = location.replace(i, localI, localJ);
    swapped = location.replace(j, localJ, localI);
  }

  /**
  * Computes the index of the element's left child
  * @param parent index of element in list
  * @return index of element's left child in list
  */
  private int left(int parent) {
    return (2*parent)+1;
  }

  /**
  * Computes the index of the element's right child
  * @param parent index of element in list
  * @return index of element's right child in list
  */
  private int right(int parent) {
    return (2*parent)+2;
  }

  /**
  * Computes the index of the element's parent
  * @param child index of element in list
  * @return index of element's parent in list
  */

  private int parent(int child) {
    return (child-1)/2;
  }


  /*********************************************************
  *     These are optional private methods that may be useful
  *********************************************************/


  /**
  * Push down the root element
  * @return the index in the list where the element is finally stored
  */
  private int pushDownRoot() {
    return pushDown(0);
  }

  /**
  * Percolate up the last leaf in the heap, i.e. the most recently
  * added element which is stored in the last slot in the list
  *
  * @return the index in the list where the element is finally stored
  */
  private int percolateUpLeaf(){
    return percolateUp(heap.size()-1);
  }

  /**
  * Returns true if element is a leaf in the heap
  * @param i index of element in heap
  * @return true if element is a leaf
  */
  private boolean isLeaf(int i){
    if (left(i) > heap.size()-1){
      return true;
    }
    return false;
}

/**
* Returns true if element has two children in the heap
* @param i index of element in the heap
* @return true if element in heap has two children
*/
private boolean hasTwoChildren(int i) {
  if (right(i) > heap.size()-1){
    return false;
  }
  return true;
}

/**
* Print the underlying list representation
*/
private void printHeap() {
  System.out.println("Heres the priortiy queue:");
  for(int i = 0; i < heap.size(); i++){
    System.out.println(heap.get(i).priority + " ---->" + heap.get(i).element);
  }
}

/**
* Print the entries in the location map
*/
private void printMap() {
  System.out.println("Heres the map:" + location.toString());
}
}
