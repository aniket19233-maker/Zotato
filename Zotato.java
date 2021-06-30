import java.util.*;
import java.io.*;
import java.lang.*;


//interface that uses default functions needed by different users as common function
interface OwnerHelper{
	
	Scanner sc = new Scanner(System.in);
	
	//function to login into owners account 
	public default void enter (Restaurant rst) 
	{
		//run a loop till the time they don't exit
		while(true) {
			
				//show powers of a owner of a restaurant
				showFacility(rst);
				
				//option to select a functionality
				int option = sc.nextInt();
				
				//option1 to add an item
				if(option == 1) {
					rst.addItem(takeNewItem());
				}
				
				//option 2 to edit an item
				else if(option == 2) {
					rst.showMenu();						//show menu
					rst.editItem(sc.nextInt());			//edit an item
				}
				
				//option 3 to print rewards earned
				else if(option == 3) {
					rst.printRewards();
				}
				
				//option 4 to get discount set of a bill set by the owner
				else if(option == 4) {
					if(rst.getCategory().equals("FastFood")) {
						System.out.println("Enter offer on Total bill value -");
						((FastFood) rst).setdiscountOnBill(sc.nextInt());
					}
					else if(rst.getCategory().equals("Authentic"))
						System.out.println("Enter offer on Total bill value -");
						((Authentic) rst).setdiscountOnBill(sc.nextInt());
				}
				
				//option 5 to exit
				else if(option == 5) {
					return;
				}
				
			}
	}
	
	//show the facility of a particluar restaurant
	public default void showFacility(Restaurant rst) {
		System.out.println("Welcome "+rst.getName()+"\n  "+"1) Add item\n  "+
		"2) Edit item\n  3) Print Rewards\n  "+"4) Discount on bill value\n  5) Exit\n");
	}
	
	//function to add a new food item 
	public default Food takeNewItem() {
		String name,category;int price,quantity,offer;
		System.out.println("Food Name:");name=sc.next();
		System.out.println("Item price:");price=sc.nextInt();
		System.out.println("item quantity:");quantity=sc.nextInt();
		System.out.println("item category:");category=sc.next();
		System.out.println("Offer:");offer=sc.nextInt();
		Food f = new Food(name,price,quantity,category,offer);f.setID();
		f.printDetails();
		return f;
	}
}


//interface that uses default functions needed by different users as common function
interface CustomerHelper{
	
	//function to login into customer's account 
	public default void enter(Customer c){
		Scanner sc = new Scanner(System.in);
		
		//run a loop till the item the customer doesn't exit 
		while(true) {
			
				//show choices available
				showChoices(c.getName());
				
				//select an option from given choices
				int option = sc.nextInt();
				
				
				if(option == 1) {
					c.selectRestaurant();
				}
				
				else if(option == 2) {
					c.checkOutCart(c,c.cart.get(c.cart.size()-1).getRestaurant());
				}
				
				else if(option == 3) {
					System.out.println(c.rewardWon());
				}
				
				else if(option == 4) {
					c.printRecentOrders();
				}
				
				else if(option == 5) {
					return;
				}
				
			}
	}
	
	//function to show choices
	public default void showChoices(String Name) {
		System.out.println("Welcome "+Name+"\nCustomer Menu");
		System.out.println("1) Select Restaurant\n2) checkout cart\n3) Reward won\n"
				+ "4) print the recent orders\n5) Exit");
	}
	
}

// class restaurant
class Restaurant {
	
	public static Scanner sc = new Scanner(System.in);
	private String owner;									//owner of restaurant 
	private String name,address,category;					// name, address, category
	private ArrayList<Food> items;							// list of items in a restaurant	
	public int discount,points,ordersTaken;                 // discount, number of orders taken
	public HashMap<Customer,Integer> reward_points;			//to keep track of restaurant orders
	
	public Restaurant(String name,String address) {
		this.setName(name);									//setName
		this.address=address;								//setaddress
		this.items = new ArrayList<Food>();		
		this.reward_points= new HashMap<Customer, Integer>();
	}
	
	//function to set name 
	public void setName(String name) {
		this.name = name;
	}
	
	//function to set catgeory 
	public void setCategory(String category) {
		this.category = category;
	}
	
	//function to add an item
	public void addItem(Food f) {
		items.add(f);
		f.setRestaurant(this);
	}
	
	//function to print rewards
	public  void printRewards() {
		System.out.println("Reward Points: "+points);
	}
	
	//function to get category
	public String getCategory() {
		return this.category;
	}
	
	////function to get discount on bill
	public void getdiscountOnBill() {
		System.out.println("Offer on bill value - "+discount);
	}
	
	//function to show the menu item 
	public void showMenu() {
		System.out.println("Choose item by code\n");
		for(int i=0;i<items.size();i++)
			items.get(i).printDetails();
	}
	
	//function to get an item by code
	public int getItembyCode(int foodOption) {
		for(int i=0;i<items.size();i++)
			if(items.get(i).getID()==foodOption)
				return i+1;
		return 0;
				
	}
	
	//function to take order
	public Food takeOrder(int opt) {
		System.out.println("Enter item quantity - ");
		items.get(opt-1).setQuantity(sc.nextInt());
		System.out.println("items added to cart");
		return items.get(opt-1);
	}
	
	////function to get total bill
	public float getTotalBill(ArrayList<Food> cart, Restaurant rt ,Customer c) {
		float total = 0.0f;
		
		for(Food f:cart) 
			total+=(f.getPrice()*f.getQuantity() - f.getPrice()*f.getQuantity()*f.getOffer()*0.01f);
		
		total -= (total*0.01f);
		
		if(c.getCategory().equals("Elite") && total>=200.0)
			total -= c.getDeliveryDiscount();
		
		else if(c.getCategory().equals("Special") && total>=200.0) {
			total -= c.getDeliveryDiscount();
			total += 20.0f;
			RestaurantOwner.DelCharges += 20.0f;
		}
		else {
			total += 40.0f;
			RestaurantOwner.DelCharges += 40.0f;
		}
		if(rt.category.equals("Authentic") && total>=100.0f) total-=50.0f;

		RestaurantOwner.compBalance += (total*0.01f);
		c.AccBalance -= total;
		
		if(rt.category.equals("FastFood")) {
			c.addRewards((int)(total/15.0));
			rt.reward_points.put(c,(int)(total/15.0));
		}
		
		else if(rt.category.equals("Authentic")) {
			c.addRewards((int)(total/8.0));
			rt.reward_points.put(c, (int)(total/8.0));}
		
		else {
			c.addRewards((int)(total/20.0));
			rt.reward_points.put(c,(int)(total/20.0));
		}
		return total;
	}
	
	//function to edit an item
	public void editItem(int foodoption) {
		
		//show editing options 
		this.showEditingOptions();
		
		//select an option
		int opt=sc.nextInt();
		
		//select item by  code
		foodoption = getItembyCode(foodoption);
		
		
		//choose function accordingly to edit its attribute
		
		if(opt == 1) {
			System.out.println("Enter the new name - ");
			items.get(foodoption-1).setName(sc.next());
			items.get(foodoption-1).printDetails();
		}
		
		else if(opt == 2) {
			System.out.println("Enter the new price - ");
			items.get(foodoption-1).setPrice(sc.nextInt());
			items.get(foodoption-1).printDetails();
		}
		
		else if(opt == 3) {
			System.out.println("Enter the new quantity - ");
			items.get(foodoption-1).setQuantity(sc.nextInt());
			items.get(foodoption-1).printDetails();
		}
		
		else if(opt == 4) {
			System.out.println("Enter the new category - ");
			items.get(foodoption-1).setCategory(sc.next());
			items.get(foodoption-1).printDetails();
		}
		
		else if(opt == 5) {
			System.out.println("Enter the new offer - ");
			items.get(foodoption-1).setOffer(sc.nextInt());
			items.get(foodoption-1).printDetails();
		}
	}
	
	//function to show editing options
	public void showEditingOptions() {
		System.out.println("Choose an attribute to edit:\n  1) Name\n"+
				"  2) Price\n  3) Quantity\n  4) Category\n  5) Exit\n");
	}
	
	//function to get details 
	public void getDetails() {
		System.out.println(this.name+" "+this.address+" "+this.ordersTaken);
	}
	
	//function to get name
	public String getName() {
		return this.name;
	}
	
	//function to get owner name
	public String getOwner() {
		return this.owner;
	}

}

//class of a customer
class Customer {
	
	static Scanner sc = new Scanner(System.in);
	private ArrayList<Food> orders;										//orders of a customer
	public ArrayList<Food> cart;										//cart to add items in a cart
	public float AccBalance;											// account balance
	private int rewards;												//rewards of a customer
	private String Name,Address,Category;								//name of a customer
	private int deliveryDiscount;										//delivery discount
	
	//constructor of a customer
	public Customer(String Name,String Category,String Address,float AccBalance) {
		this.Name = Name;
		this.Category = Category;
		this.Address = Address;
		this.AccBalance = AccBalance;
		this.cart = new ArrayList<Food>();
		this.orders = new ArrayList<Food>(); 
		this.rewards=0;
	}
	
	//function to get Name
	public String getName() {
		return this.Name;
	}
	
	//function to get Category
	public String getCategory() {
		return this.Category;
	}
	
	//function to getDetails
	public void getDetails() {
		System.out.println(this.Name+"("+this.Category+"), "+this.Address+", "+this.AccBalance+"/-");
	}
	
	//function to add rewards
	public void addRewards(int points) {
		this.rewards+=points;
	}
	
	//function to select restaurant 
	public void selectRestaurant() {
		Application_Manager.showRestaurantChoices();
		int opt=sc.nextInt();
		RestaurantOwner.list[opt-1].showMenu();
		this.addToCart(RestaurantOwner.list[opt-1].takeOrder(sc.nextInt()));
	}
	
	////function to add to cart
	public void addToCart(Food f) {
		this.cart.add(f);
		this.orders.add(f);
	}
	
	
	//function to check out of cart
	public void checkOutCart(Customer c,Restaurant rt) {
		System.out.println("Items in Cart -");
		int count=0;
		for(Food f : cart) {f.printDetails();count+=f.getQuantity();}
		float bill=rt.getTotalBill(cart, rt, c);
		System.out.println("Delivery Charge - "+ "0 INR /- \n"+"Total Order Value - INR "+
		 bill+" /-\n"+"  1) Proceed to checkout");sc.nextInt();
		System.out.println(count+" items successfully bought for INR "+bill+" /-");
		this.cart.clear();
	}
	
	public int rewardWon() {
		return this.rewards; //rewards
	}
	
	//function to print recent orders
	public void printRecentOrders() {
		for(int i = orders.size()-1; i>=(orders.size()<=10?0:orders.size()-10);i--)
			orders.get(i).printDetails();
	}
	
	//function to get Delivery discount
	public int getDeliveryDiscount() {
		return this.deliveryDiscount;
	}
}

//food class

class Food{
	
	private static int count;							//static count to set id's of food
	private int ID; 									// id of a food item
	private int Price, Quantity, Offer;					//price category quantity offer on that item
	private String Name, Category;						//name of the item
	private Restaurant restaurant;						//belongs to which restaurant
	
	//food class constructor
	public Food(String Name, int Price, int Quantity, String Category,int Offer) {
		this.setPrice(Price);							//setPrice
		this.setName(Name);								//setName
		this.setQuantity(Quantity);						//setQuantity	
		this.setCategory(Category);						//setCategory
		this.setOffer(Offer);							//setOffer
	}
	
	//function to set ID
	void setID() {
		this.ID=++count; 
	}
	//function to set restaurant
	public void setRestaurant(Restaurant rt) {
		this.restaurant = rt;
	}
	//function to set Name
	public void setName(String Name) {
		this.Name = Name; 
	}
	//function to set Price
	
	public void setPrice(int Price) {
		this.Price =  Price; 
	}
	//function to set Quantity
	public void setQuantity(int Quantity) {
		this.Quantity = Quantity; 
	}
	
	public void setOffer(int Offer) {
		this.Offer = Offer;
	}
	
	//function to set Category
	public void setCategory(String Category) {
		this.Category = Category; 
	}
	
	//function to getOffer
	public int getOffer() {
		return this.Offer;
	}
	
	//function to getId
	public int getID() {
		return this.ID;
	}

	//function to getRestaurant
	public Restaurant getRestaurant() {
		return this.restaurant;
	}
	
	//function to getName
	public String getName() { 
		return this.Name; 
	}
	
	//function to get price
	public int getPrice(){ 
		return this.Price; 
	}
	
	//function to get quantity
	public int getQuantity() {
		return this.Quantity;
	}
	
	//function to get category
	
	public String getCategory() { 
		return this.Category; 
	}
	
	//function to print food details
	public void printDetails() {
		
		System.out.println(this.getID() +" "+this.Name+" "+this.Price+" "+
		this.Quantity+" "+this.Offer+" % off "+this.Category);
	}
	
}

//class of a company of restaurant owners that has signed a contract with Owner helper interface 
class RestaurantOwner implements OwnerHelper{

	public Scanner sc = new Scanner(System.in);
	public static Restaurant list[]; 
	static float compBalance=0,DelCharges=0;
	
	//restaurant owner constructor
	public RestaurantOwner() {
		this.list= new Restaurant[5];				//list of restaurants available
		this.setAutheticRestaurants();
		this.setFastFoodRestaurants();
		this.setOrdinaryRestaurants();
	}
	
	private void setFastFoodRestaurants() {
		this.list[3] = new FastFood("Wang's","");
	}
	
	private void setAutheticRestaurants() {
		this.list[0] = new Authentic("Shah","");
		this.list[2] = new Authentic("The Chinese","");
	}
	
	private void setOrdinaryRestaurants() {
		this.list[1] = new Ordinary("Ravi's","");
		this.list[4] = new Ordinary("Paradise","");
	}
	
	//function to give a restaurant list
	private void giveRestaurantList() {
		for(int i=0;i<list.length;i++)
			System.out.println((i+1)+". "+list[i].getName());
	}
	
	//search for a restaurant
	public void LookForRestaurant() {
		this.giveRestaurantList();
		this.list[sc.nextInt()-1].getDetails();
	}
	
	//give company account balance
	public void CompanyAccountDetails() {
		System.out.println("Total Company balance - "+this.compBalance);
		System.out.println("Total Delivery Charges Collected - "+this.DelCharges);
	}
	
	//choose restaurant 
	public void chooseRestaurant(int opt) {
		enter(list[opt-1]);
	}
	
	//add to company balance
	public void addToCompanyBalance(float amount) {
		this.compBalance += amount;
	}
	
	//add to delivery charges
	public void addToDeliveryCharges(float amount) {
		this.DelCharges += amount;
	}
	
}

//fastfood restaurant
class FastFood extends Restaurant implements OwnerHelper{
	public FastFood(String name,String address) {
		super(name,address);
		setCategory("FastFood");
	}
	
	public void setdiscountOnBill(int discount) {
		this.discount = discount;
	}
}

//authentic restaurant class
class Authentic extends Restaurant implements OwnerHelper{
	public Authentic(String name,String address) {
		super(name,address);
		setCategory("Authentic");
	}
	
	public void setdiscountOnBill(int discount) {
		this.discount = discount;
	}
	
	public int getDiscount() {
		return this.discount;
	}
}

//ordinary restaurant class
class Ordinary extends Restaurant implements OwnerHelper{
	public Ordinary(String name,String address) {
		super(name,address);
		setCategory("Ordinary");
	}
}

//ELite customer class
class Elite extends Customer implements CustomerHelper{
	public Elite(String Name,String Category,String Address, int AccBalance) {
		super( Name, Category,Address, AccBalance);
	}
	
	public void payMoney(float amount) {
		this.AccBalance-=amount;
	}
}

//Special customer class
class Special extends Customer implements CustomerHelper{
	public Special(String Name,String Category,String Address, int AccBalance) {
		super( Name, Category,Address, AccBalance);
	}
	
	public void payMoney(float amount) {
		this.AccBalance-=amount;
	}
}

//simple customer class
class Simple extends Customer implements CustomerHelper{
	public Simple(String Name,String Category,String Address, int AccBalance) {
		super( Name, Category,Address, AccBalance);
	}
	
	public void payMoney(float amount) {
		this.AccBalance-=amount;
	}
}


//customer manager that deals with customer and gives all details 
class CustomerManager implements CustomerHelper{
	
	public Scanner sc = new Scanner(System.in);
	private Customer ctList[];
	
	public CustomerManager() {
		this.ctList = new Customer[5];
		this.setEliteCustomers();
		this.setSpecialCustomers();
		this.setSimpleCustomers();
	}
	
	public void setEliteCustomers() {
		this.ctList[0] = new Elite("Ram", "Elite","Pune",1000);
		this.ctList[1] = new Elite("Sam", "Elite","Mumbai",1000);
	}
	
	public void setSpecialCustomers() {
		this.ctList[2] = new Special("Tim","Special","Punjab",1000);
	}
	public void setSimpleCustomers() {
		this.ctList[3] = new Simple("Kim","Simple","Ludhiana",1000);
		this.ctList[4] = new Simple("Jim","Simple","Chandigarh",1000);
	}
	
	public Customer getCustomer(int opt) {
		return this.ctList[opt-1];
	}
	
	public void printAllCustomers() {
		for(int i=0;i<ctList.length;i++)
			System.out.println((i+1)+". "+ctList[i].getName()+"("+ctList[i].getCategory()+")");
	}
	
	public void checkDetails() {
		this.printAllCustomers();
		this.ctList[sc.nextInt()-1].getDetails();
	}
}

//application manager that runs the app
class Application_Manager {
	
	private RestaurantOwner rm;
	private CustomerManager ctm;
	public Scanner sc = new Scanner(System.in);
	
	public Application_Manager() {
		this.ctm= new CustomerManager();
		this.rm = new RestaurantOwner();
		this.runApplication();
	}
	
	//function to run the application
	public void runApplication() {
		
		while(true) {
			
			this.showMessage();

			int option = sc.nextInt();
			
			if(option == 1) {
				this.showRestaurantChoices();
				rm.chooseRestaurant(sc.nextInt());
			}
			
			else if(option == 2) {
				ctm.printAllCustomers();
				ctm.enter(ctm.getCustomer(sc.nextInt()));
			}
			
			else if(option == 3) {
				System.out.println("1) Customer List\n2) Restaurant List");
				this.checkUserDetails();
			}
			
			else if(option == 4) {
				rm.CompanyAccountDetails();
			}
			
			else if(option == 5) {
				return;
			}
		}
	}
	
	
	//shows Opening message when application opens with functionalities available
	private void showMessage() {
		System.out.println("Welcome to Zotato: \n   1. Enter as Restaurant Owner\n   2. Enter as Customer\n"
				+"   3. Enter User Details\n   4. Company Account Details\n   5. Exit");
	}
	
	//function to show RestaurantChoices
	public static void showRestaurantChoices() {
		System.out.println("Choose Restaurant\n   1) Shah (Authentic)\n   2) Ravi's");
		System.out.println("   3) The Chinese (Authentic)\n   4) Wang's (Fast Food)\n   5) Paradise");
	}
	
	//function to check user details
	private void checkUserDetails() {
		int opt = sc.nextInt();
		if(opt == 1) ctm.checkDetails();
		else if(opt ==2 ) rm.LookForRestaurant();
	}
}

public class Zotato {

	public static void main(String[] args)
	{
		//object to run the application for Zotato
		Application_Manager am = new Application_Manager();
	}

}
