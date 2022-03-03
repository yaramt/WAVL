

/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree.
 * (Haupler, Sen & Tarajan ‘15)
 */

public class WAVLTree {
	private WAVLNode root;
	private WAVLNode virtualNode;
	private WAVLNode min;
	private WAVLNode max;

	public WAVLTree() {
		virtualNode = new WAVLNode(-1, null);
		virtualNode.setRank(-1);
		root = virtualNode;
		min = null;
		max = null;

	}

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty
	 */
	public boolean empty() {
		return (root == virtualNode);
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree
	 * otherwise, returns null
	 */
	public String search(int k) {
		WAVLNode node = this.root;
		while (node != virtualNode) {
			if (node.getKey() == k) {
				return node.getValue();
			}
			if (k > node.getKey()) {
				node =  node.getRight();
			} else {
				node =  node.getLeft();
			}

		}
		return node.getValue(); // node == vitualNode && virtualNode.getValue ==
								// null
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the WAVL tree. the tree must
	 * remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were necessary. returns -1
	 * if an item with key k already exists in the tree.
	 *
	 */
	public int insert(int k, String i) {
		// checking if already exist
		if (searchTheNode(k) != virtualNode)
			return -1;

		WAVLNode node = new WAVLNode(k, i, virtualNode);
		// checking if empty
		if (empty()) {
			this.root = node;
			min = node;
			max = node;
			return 0;
		}
		// updating minimum and maximum
		if (node.key > max.key) {
			max = node;
		}
		if (node.key < min.key) {
			min = node;
		}

		// insert the new value into the tree and rebalance
		WAVLNode iter = this.root;
		WAVLNode prev = iter;
		while (iter!= virtualNode) {
			prev = iter;
			iter.size++;
			if (k > iter.getKey())
				iter =  iter.getRight();
			else
				iter =  iter.getLeft();

		}
		if (k > prev.getKey())
			prev.setRight(node);
		else
			prev.setLeft(node);
		node.setParent(prev);

		return rebalance(node);

	}

	/**
	 * complexity is O(log n) - traversing the height of the tree. k is the key
	 * to find. returns (node != virtualNode) iff node.key == k, null if not
	 * found
	 */
	private WAVLNode searchTheNode(int k) {
		WAVLNode i = this.root;
		while (i != virtualNode) {
			if (i.getKey() == k)
				return i;
			if (k > i.getKey())
				i =  i.getRight();
			else
				i =  i.getLeft();

		}
		return i;
	}

	/**
	 * rebalance the tree according to the WAVL rules. complexity is O(log n) as
	 * we traverse the tree upwards once.
	 * 
	 */
	private int rebalance(WAVLNode node) {

		if (node.parent == null || node.parent.rank != node.rank)
			return 0;
		WAVLNode parent = node.parent;

		int difference;

		if (parent.right == node) {
			difference = parent.rank - parent.left.rank;
		} else {
			difference = parent.rank - parent.right.rank;
		}

		// case 1 (promote)
		if (difference == 1) {
			parent.rank++;
			return 1 + rebalance(parent);
		}

		// case 2 (sons rank of x are 1/2)

		if (parent.right == node) {
			difference = node.rank - node.left.rank;
		} else {
			difference = node.rank - node.right.rank;
		}

		if (difference == 2) {
			if (parent.right == node) {
				return rotateToLeft(node);
			} else {
				return rotateToRight(node);
			}

		}

		// case3 (sons rank of x are 2/1)
		if (parent.right == node) {
			node = node.left;
			node.rank++;
			return 1 + rotateToRight(node) + rotateToLeft(node);
		} else {
			node = node.right;
			node.rank++;
			return 1 + rotateToLeft(node) + rotateToRight(node);
		}

	}

	/**
	 * rotate the tree right complexity is O(1)
	 * 
	 */
	private int rotateToRight(WAVLNode node) {

		WAVLNode parent = node.parent;

		node.size = parent.size;
		parent.size -= (node.left.size + 1);
		parent.rank--;
		parent.left = node.right;
		node.right.parent = parent;
		node.right = parent;
		node.parent = parent.parent;
		parent.parent = node;

		// check if parent is root
		if (parent == root)
			root = node;
		else {
			if (node.parent.right == parent)
				node.parent.right = node;
			else
				node.parent.left = node;
		}

		return 1;
	}

	/**
	 * rotate the tree right complexity is O(1)
	 * 
	 */
	private int rotateToLeft(WAVLNode node) {

		WAVLNode parent = node.parent;
		node.size = parent.size;
		parent.size -= (node.right.size + 1);
		parent.rank--;
		parent.right = node.left;
		node.left.parent = parent;
		node.left = parent;
		node.parent = parent.parent;
		parent.parent = node;

		// check if parent is root
		if (parent == root)
			root = node;
		else {
			if (node.parent.right == parent)
				node.parent.right = node;
			else
				node.parent.left = node;
		}

		return 1;
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of
	 * rebalancing operations, or 0 if no rebalancing operations were needed.
	 * returns -1 if an item with key k was not found in the tree.
	 *
	 */
	public int delete(int k) {
		WAVLNode node = searchTheNode(k);
		if (node == virtualNode)
			return -1;

		// update max min
		if (node == max) {
			if (max.left == virtualNode)
				max = max.parent;
			else {
				max = getRightMostSon(max.left);
			}
		}
		if (node == min) {
			if (min.right == virtualNode)
				min = min.parent;
			else {
				min = getLeftMostSon(min.right);
			}
		}

		// check if its a unary node or a leaf
		if (node.right != virtualNode)
			replace(node, node = getLeftMostSon(node.right));
		// update the size
		updateRoot(node.parent, -1);
		// node is now a unary node or a leaf

		WAVLNode parent = node.parent;
		WAVLNode son;
		// delete the node
		if (node.right == virtualNode) {
			son = node.left;
			if (son != virtualNode)
				son.parent = parent;
			if (parent == null) {
				root = son;
			} else {
				if (parent.left == node)
					parent.left = son;
				else
					parent.right = son;
			}
		} else {
			son = node.right;
			if (son != virtualNode)
				son.parent = parent;
			if (parent == null) {
				root = son;
			} else {
				if (parent.left == node)
					parent.left = son;
				else
					parent.right = son;
			}

		}

		// if parent is a leaf update the rank to zero
		if (parent != null && parent.right == virtualNode && parent.left == virtualNode) {
			parent.rank = 0;
			return 1 + rebalanceDelete(parent.parent, parent);
		}

		return rebalanceDelete(parent, son);

	}

	/**
	 * update size of all nodes up to root complexity is O(log n)
	 * 
	 */
	private void updateRoot(WAVLNode node, int i) {
		while (node != null) {
			node.size += i;
			node = node.parent;
		}

	}

	/**
	 * rebalances the tree after deletion. complexity is O(log n) as we traverse
	 * the tree upwards at most once, and usually we don't at all. return the
	 * number of rebalancing operations.
	 */
	private int rebalanceDelete(WAVLNode parent, WAVLNode son) {
		if (parent == null || (parent.rank - son.rank) != 3)
			return 0;

		WAVLNode otherSon;
		if (son == parent.left)
			otherSon = parent.right;
		else
			otherSon = parent.left;

		// case1
		if ((parent.rank - otherSon.rank) == 2) {
			parent.rank--;
			return 1 + rebalanceDelete(parent.parent, parent);
		}

		// case2
		if ((otherSon.rank - otherSon.right.rank) == 2 && (otherSon.rank - otherSon.left.rank) == 2) {
			parent.rank--;
			otherSon.rank--;
			return 2 + rebalanceDelete(parent.parent, parent);
		}
		// case3
		if (otherSon == parent.right) {
			if ((otherSon.rank - otherSon.right.rank) == 1) {
				rotateToLeft(otherSon);
				otherSon.rank++;
				if (parent.right == virtualNode && parent.left == virtualNode) {
					parent.rank = 0;
					return 3;
				}
				return 2;
			}
			// case4
			else {
				WAVLNode sonOfSon = otherSon.left;
				rotateToRight(sonOfSon);
				rotateToLeft(sonOfSon);
				sonOfSon.rank += 2;
				parent.rank--;
				return 3;

			}
		}
		// symmetric 
		else {
			if ((otherSon.rank - otherSon.left.rank) == 1) {
				rotateToRight(otherSon);
				otherSon.rank++;
				if (parent.right == virtualNode && parent.left == virtualNode) {
					parent.rank = 0;
					return 3;
				}
				return 2;
			}
			// case4
			else {
				WAVLNode sonOfSon = otherSon.right;
				rotateToLeft(sonOfSon);
				rotateToRight(sonOfSon);
				sonOfSon.rank += 2;
				parent.rank--;
				return 3;
			}
		}

	}

	/**
	 * replaces a node with the given node.
	 * 
	 */
	private void replace(WAVLNode node, WAVLNode successor) {

		if (successor == max) {
			max = node;
		}
		if (successor == min) {
			min = node;
		}

		node.value = successor.value;
		node.key = successor.key;
	}

	/**
	 * complexity at most O(log n) only 1 tree traversal
	 * 
	 */
	private WAVLNode getLeftMostSon(WAVLNode node) {
		while (node.left != virtualNode)
			node = node.left;
		return node;
	}

	/**
	 * complexity at most O(log n) - only 1 tree traversal
	 * 
	 */
	private WAVLNode getRightMostSon(WAVLNode node) {
		while (node.right != virtualNode)
			node = node.right;
		return node;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null
	 * if the tree is empty
	 */
	public String min() {
		if (min == null)
			return null;
		return min.value;
	}
public int getmin() {
	if (min == null)
		return 0;
	return min.getKey();
}
	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if
	 * the tree is empty
	 *
	 */
	public String max() {
		if (max == null)
			return null;
		return max.value;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty
	 * array if the tree is empty.
	 *
	 */
	public int[] keysToArray() {
		int[] arr = new int[size()];
		fillKeysRec(arr, root, 0);
		return arr;
	}

	/**
	 * fills the keys into an array complexity - O(n) - iterating through the
	 * entire tree a helping function, we recursively solve the problem.
	 */
	private int fillKeysRec(int[] arr, WAVLNode iter, int i) {
		if (iter == virtualNode)
			return i;
		i = fillKeysRec(arr, iter.left, i);
		arr[i++] = iter.key;
		i = fillKeysRec(arr, iter.right, i);
		return i;

	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 *
	 * 
	 */
	public String[] infoToArray() {
		String[] arr = new String[size()];
		fillInfoRec(arr, root, 0);
		return arr;
	}

	/**
	 * fills the values into an array complexity - O(n) - iterating through the
	 * entire tree RECURSIVELY
	 * 
	 */
	private int fillInfoRec(String[] arr, WAVLNode iter, int i) {
		if (iter == virtualNode)
			return i;
		i = fillInfoRec(arr, iter.left, i);
		arr[i++] = iter.value;
		i = fillInfoRec(arr, iter.right, i);
		return i;

	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {
		if (empty())
			return 0;
		return root.size;
	}

	/**
	 * public int getRoot()
	 *
	 * Returns the root WAVL node, or null if the tree is empty
	 *
	 * precondition: none postcondition: none complexity is O(1)
	 */
	public WAVLNode getRoot() {
		if (root == virtualNode)
			return null;
		return root;
	}

	public WAVLNode selectRec(int i, WAVLNode subtreeMin) {
		WAVLNode current = subtreeMin;
		while (current.getSubtreeSize() < i) {
			current =  current.getParent();
		}
		i = i - current.getLeft().getSubtreeSize();
		if (i == 1)
			return current;
		WAVLNode successor =  current.getRight();
		assert successor != virtualNode && successor != null;
		while (successor.getLeft() != virtualNode) {
			successor =  successor.getLeft();
		}
		return selectRec(i - 1, successor);
	}

	/**
	 * public int select(int i)
	 * <p>
	 * Returns the value of the i'th smallest key (return -1 if tree is empty)
	 * Example 1: select(1) returns the value of the node with minimal key
	 * Example 2: select(size()) returns the value of the node with maximal key
	 * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the
	 * value of the node minimal node's successor
	 * <p>
	 * precondition: size() >= i > 0 postcondition: none
	 */
	public String select(int i) {
		// tree not empty. start with min
		if (empty()) {
			return null;
		}
		if (i == size() + 1)
			return max.getValue();
		return selectRec(i, min).getValue();
	}
/**
 * public class WAVLNode
 *
 * If you wish to implement classes other than WAVLTree (for example
 * WAVLNode), do it in this file, not in another file. This class can and
 * must be modified. (It must implement IWAVLNode)
 */
public class WAVLNode {

	public int key;
	public String value;
	public WAVLNode right;
	public WAVLNode left;
	public WAVLNode parent;
	public int rank;

	// size is the number of nodes in the subtrees of this node plus 1
	public int size;

	// Builder for virtual node
	public WAVLNode(int key, String value) {
		this.key = key;
		this.value = value;
		this.right = null;
		this.left = null;
		this.rank = -1;
		this.parent = null;
		this.size = 0;
	}

	// Builder for leaf
	public WAVLNode(int key, String value, WAVLNode virtualNode) {
		this.key = key;
		this.value = value;
		this.left = virtualNode;
		this.right = virtualNode;
		this.parent = null;
		this.rank = 0;
		this.size = 1;

	}
	public void setRank(int rank) {
		this.rank = rank;
	}

	public void setLeft(WAVLNode node) {
		this.left = node;
	}

	public void setRight(WAVLNode node) {
		this.right = node;
	}

	public void setParent(WAVLNode parent) {
		this.parent = parent;
	}

	

	public int getKey() {
		return this.key; 
	}

	public String getValue() {
		return this.value; 
	}

	public WAVLNode getLeft() {
		return this.left;
	}

	public WAVLNode getRight() {
		return this.right;
	}

	public WAVLNode getParent() {
		return this.parent;
	}

	public int getRank() {
		return rank;
	}

	public boolean isInnerNode()
    {
           if(this.left!=null && this.right!=null)
        	   return true;
           return false;
    }

	public int getSubtreeSize() {
		return size; // to be replaced by student code
	}
	
			
	}}




