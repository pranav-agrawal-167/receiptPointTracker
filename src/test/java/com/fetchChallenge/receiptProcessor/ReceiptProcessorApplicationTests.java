package com.fetchChallenge.receiptProcessor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class ReceiptProcessorApplicationTests {

	private ReceiptProcessorApplication receiptProcessor;

	@BeforeEach
	void setUp() {
		receiptProcessor = new ReceiptProcessorApplication();
	}

	@Test
	void contextLoads() {
	}

	@Test
	void testProcessReceipt() {
		Receipt receipt = mock(Receipt.class);
		ResponseEntity<?> responseEntity = receiptProcessor.processReceipt(receipt);

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());
	}

	@Test
	void testGetPointsWithValidReceipt() {
		Receipt receipt = mock(Receipt.class);

		receiptProcessor.getReceiptStore().put("validId", receipt);
		when(receipt.getRetailer()).thenReturn("Retailer");
		when(receipt.getTotal()).thenReturn(10.0);
		when(receipt.getPurchaseDate()).thenReturn(LocalDate.now());
		when(receipt.getPurchaseTime()).thenReturn(LocalTime.now());

		ResponseEntity<?> responseEntity = receiptProcessor.getPoints("validId");

		assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		assertNotNull(responseEntity.getBody());

		receiptProcessor.getReceiptStore().remove("validId");
	}

	@Test
	void testGetPointsWithInvalidReceipt() {
		ResponseEntity<?> responseEntity = receiptProcessor.getPoints("invalidId");

		assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
	}

	@Test
	void testCalculatePoints() {
		// Create a mock Receipt object
		Receipt receipt = mock(Receipt.class);
		List<Item> testItemList = createMockItemList();

		// Set up the mocked behavior for the Receipt object
		when(receipt.getRetailer()).thenReturn("Example Retailer");
		when(receipt.getTotal()).thenReturn(100.0);
		when(receipt.getItems()).thenReturn(testItemList);
		when(receipt.getPurchaseDate()).thenReturn(LocalDate.of(2023, 6, 1));
		when(receipt.getPurchaseTime()).thenReturn(LocalTime.of(15, 30));

		// Call the calculatePoints method
		int points = receiptProcessor.calculatePoints(receipt);

		// Verify the expected points value
		assertEquals(123, points);
	}

	public List<Item> createMockItemList() {
		List<Item> itemList = new ArrayList<>();

		// Add mock items to the list
		Item item1 = mock(Item.class);
		when(item1.getDescription()).thenReturn("Item 1");
		when(item1.getPrice()).thenReturn(10.0);
		itemList.add(item1);

		Item item2 = mock(Item.class);
		when(item2.getDescription()).thenReturn("Item 2");
		when(item2.getPrice()).thenReturn(20.0);
		itemList.add(item2);

		Item item3 = mock(Item.class);
		when(item3.getDescription()).thenReturn("Item 3");
		when(item3.getPrice()).thenReturn(30.0);
		itemList.add(item3);

		return itemList;
	}

}
