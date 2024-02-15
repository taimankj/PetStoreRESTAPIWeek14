package pet.store.controller.model;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Data
@NoArgsConstructor
public class PetStoreData {
	private Long petStoreId;
	private String storeName;
	private String storeAddress;
	private String city;
	private String state;
	private Long zip;
	private Long storePhone;
	private Set<PetStoreCustomer> customers = new HashSet<PetStoreCustomer>();
	private Set<PetStoreEmployee> employees = new HashSet<PetStoreEmployee>();

	public PetStoreData(PetStore petStore) {
		petStoreId = petStore.getPetStoreId();
		storeName = petStore.getStoreName();
		storeAddress = petStore.getStoreAddress();
		city = petStore.getCity();
		state = petStore.getState();
		zip = petStore.getZip();
		storePhone = petStore.getStorePhone();

		for (Customer customer : petStore.getCustomers()) {
			customers.add(new PetStoreCustomer(customer));
		}

		for (Employee employee : petStore.getEmployees()) {
			employees.add(new PetStoreEmployee(employee));
		}
	}

	@Data
	@NoArgsConstructor
	static class PetStoreCustomer {
		private Long customerId;
		private String firstName;
		private String lastName;
		private String email;

		public PetStoreCustomer(Customer customer) {
			customerId = customer.getCustomerId();
			firstName = customer.getFirstName();
			lastName = customer.getLastName();
			email = customer.getEmail();
		}
	}

	@Data
	@NoArgsConstructor
	static class PetStoreEmployee {
		private Long employeeId;
		private String firstName;
		private String lastName;
		private Long employeePhone;
		private String jobTitle;

		public PetStoreEmployee(Employee employee) {
			employeeId = employee.getEmployeeId();
			firstName = employee.getFirstName();
			lastName = employee.getLastName();
			employeePhone = employee.getEmployeePhone();
			jobTitle = employee.getJobTitle();
		}
	}
}
