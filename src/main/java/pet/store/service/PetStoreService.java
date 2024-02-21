package pet.store.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pet.store.controller.model.PetStoreData;
import pet.store.controller.model.PetStoreData.PetStoreCustomer;
import pet.store.controller.model.PetStoreData.PetStoreEmployee;
import pet.store.dao.CustomerDao;
import pet.store.dao.EmployeeDao;
import pet.store.dao.PetStoreDao;
import pet.store.entity.Customer;
import pet.store.entity.Employee;
import pet.store.entity.PetStore;

@Service
public class PetStoreService {

	@Autowired
	private PetStoreDao petStoreDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private CustomerDao customerDao;
	
	@Transactional(readOnly = false)
	public PetStoreData savePetStore(PetStoreData petStoreData) {
		PetStore petStore = findOrCreatePetStore(petStoreData.getPetStoreId());
		copyPetStoreFields(petStore, petStoreData);
		return new PetStoreData(petStoreDao.save(petStore));
	}

	private void copyPetStoreFields(PetStore petStore, PetStoreData petStoreData) {
		petStore.setPetStoreId(petStoreData.getPetStoreId());
		petStore.setStoreName(petStoreData.getStoreName());
		petStore.setStoreAddress(petStoreData.getStoreAddress());
		petStore.setCity(petStoreData.getCity());
		petStore.setState(petStoreData.getState());
		petStore.setZip(petStoreData.getZip());
		petStore.setStorePhone(petStoreData.getStorePhone());
	}

	private PetStore findOrCreatePetStore(Long petStoreId) {
		PetStore petStore;
		
		if (Objects.isNull(petStoreId)) {
			petStore = new PetStore();
		} else {
			petStore = findPetStoreById(petStoreId);
		}
		
		return petStore;
	}
	
	@Transactional(readOnly = true)
	public PetStore findPetStoreById(Long petStoreId) {
		return petStoreDao.findById(petStoreId)
				.orElseThrow(() -> new NoSuchElementException("Pet store with ID=" + petStoreId + " was not found."));
	}
	
	@Transactional(readOnly = false)
	public PetStoreEmployee saveEmployee(Long petStoreId, PetStoreEmployee petStoreEmployee) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long employeeId = petStoreEmployee.getEmployeeId();
		Employee employee = findOrCreateEmployee(petStoreId, employeeId);
		
		copyEmployeeFields(employee, petStoreEmployee);
		
		employee.setPetStore(petStore);
		petStore.getEmployees().add(employee);
		
		Employee dbEmployee = employeeDao.save(employee);
		return new PetStoreEmployee(dbEmployee);
		
		
	}

	private void copyEmployeeFields(Employee employee, PetStoreEmployee petStoreEmployee) {
		employee.setEmployeeId(petStoreEmployee.getEmployeeId());
		employee.setFirstName(petStoreEmployee.getFirstName());
		employee.setLastName(petStoreEmployee.getLastName());
		employee.setEmployeePhone(petStoreEmployee.getEmployeePhone());
		employee.setJobTitle(petStoreEmployee.getJobTitle());
	}

	private Employee findOrCreateEmployee(Long petStoreId, Long employeeId) {
		Employee employee;
		
		if (Objects.isNull(employeeId)) {
			employee = new Employee();
		} else {
			employee = findEmployeeById(petStoreId, employeeId);
		}
		
		return employee;
	}

	private Employee findEmployeeById(Long petStoreId, Long employeeId) {
		Employee employee = employeeDao.findById(employeeId).
				orElseThrow(() -> new NoSuchElementException("Employee with ID=" + employeeId + " was not found."));
		
		if (employee.getPetStore().getPetStoreId() != petStoreId) {
			throw new IllegalArgumentException("Pet store ID=" + petStoreId + " is not associated with employee ID=" + employeeId);
		} else {
			return employee;
		}
	}

	@Transactional(readOnly = false)
	public PetStoreCustomer saveCustomer(Long petStoreId, PetStoreCustomer petStoreCustomer) {
		PetStore petStore = findPetStoreById(petStoreId);
		Long customerId = petStoreCustomer.getCustomerId();
		Customer customer = findOrCreateCustomer(petStoreId, customerId);
		
		setCustomerFields(customer, petStoreCustomer);
		
		customer.getPetStores().add(petStore);
		petStore.getCustomers().add(customer);
		
		Customer dbCustomer = customerDao.save(customer);
		return new PetStoreCustomer(dbCustomer);
	}

	private void setCustomerFields(Customer customer, PetStoreCustomer petStoreCustomer) {
		customer.setCustomerId(petStoreCustomer.getCustomerId());
		customer.setFirstName(petStoreCustomer.getFirstName());
		customer.setLastName(petStoreCustomer.getLastName());
		customer.setEmail(petStoreCustomer.getEmail());
	}

	private Customer findOrCreateCustomer(Long petStoreId, Long customerId) {
		Customer customer;
		
		if (Objects.isNull(customerId)) {
			customer = new Customer();
		} else {
			customer = findCustomerById(petStoreId, customerId);
		}
		
		return customer;
	}

	private Customer findCustomerById(Long petStoreId, Long customerId) {
		Customer customer = customerDao.findById(customerId).
				orElseThrow(() -> new NoSuchElementException("Customer with ID=" + customerId + " was not found."));
		
		for (PetStore petStore : customer.getPetStores()) {
			if (petStore.getPetStoreId() == petStoreId) {
				return customer;
			}
		}
		
		throw new IllegalArgumentException("Pet store ID=" + petStoreId + " is not associated with customer ID=" + customerId);
	}

	@Transactional(readOnly = true)
	public List<PetStoreData> retrieveAllPetStores() {
		List<PetStore> petStores = petStoreDao.findAll();
		List<PetStoreData> result = new LinkedList<PetStoreData>();
		
		for (PetStore petStore : petStores) {
			PetStoreData petStoreData = new PetStoreData(petStore);
			
			petStoreData.getCustomers().clear();
			petStoreData.getEmployees().clear();
			
			result.add(petStoreData);
		}
		
		return result;
	}

	public Map<String, String> deletePetStoreById(Long petStoreId) {
		PetStore petStore = findPetStoreById(petStoreId);
		petStoreDao.delete(petStore);
		return Map.of("message", "Pet store with ID=" + petStoreId + " deleted.");
	}
	
}
