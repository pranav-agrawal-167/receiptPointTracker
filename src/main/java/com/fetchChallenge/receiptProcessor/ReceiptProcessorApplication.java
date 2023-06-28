package com.fetchChallenge.receiptProcessor;

import org.json.simple.JSONObject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;

@SpringBootApplication
@RestController
@RequestMapping("/receipts")
public class ReceiptProcessorApplication {

	private final Map<String, Receipt> receiptStore = new HashMap<>();
	private final Logger logger = Logger.getLogger(ReceiptProcessorApplication.class.getName());
	private final int POINTS_FOR_ROUND_DOLLAR_AMOUNT = 50;
	private final int POITNS_FOR_MULTIPLE_OF_QUARTER = 25;
	private final int POINTS_FOR_TWO_ITEMS = 5;
	private final double MULTIPLIER_FOR_DESCRIPTION_LENGTH = 0.2;
	private final int POINTS_FOR_ODD_PURCHASE_DAY = 6;
	private final int POINTS_FOR_PURCHASE_TIME = 10;

	public static void main(String[] args) {
		SpringApplication.run(ReceiptProcessorApplication.class, args);
	}

	@PostMapping("/process")
	public ResponseEntity<?> processReceipt(@RequestBody Receipt receipt) {
		logger.info("POST API hit");
		String receiptId = generateReceiptId();
		receiptStore.put(receiptId, receipt);
		JSONObject response = new JSONObject();
		response.put("id", receiptId);
		return ResponseEntity.ok(response.toString());
	}

	@GetMapping("/{id}/points")
	public ResponseEntity<?> getPoints(@PathVariable("id") String receiptId) {
		logger.info("GET API hit");
		Receipt receipt = receiptStore.get(receiptId);
		if(receipt == null) {
			return ResponseEntity.notFound().build();
		}

		int points = calculatePoints(receipt);
		JSONObject response = new JSONObject();
		response.put("points", points);
		return ResponseEntity.ok(response.toString());
	}

	public int calculatePoints(Receipt receipt) {
		int points = 0;

		// Rule 1: One point for each alphanumeric character in retailer name.
		if(receipt.getRetailer() != null) {
			points += receipt.getRetailer().replaceAll("[^a-zA-Z0-9]", "").length();
		}

		// Rule 2: 50 points if the total is a round dollar amount with no cents.
		if(receipt.getTotal() % 1 == 0) {
			points += POINTS_FOR_ROUND_DOLLAR_AMOUNT;
		}


		// Rule 3: 25 points if the total is a multiple of 0.25.
		if(receipt.getTotal() % 0.25 == 0) {
			points += POITNS_FOR_MULTIPLE_OF_QUARTER;
		}

		// Rule 4: 5 points for every two items on the receipt.
		List<Item> itemList = receipt.getItems();
		if(!itemList.isEmpty()) {
			points += receipt.getItems().size() / 2 * POINTS_FOR_TWO_ITEMS;
			for(Item item: itemList) {
				if(item.getDescription() != null) {
					// Rule 5: If the trimmed length of the item description is a multiple of 3, multiply the price by 0.2 and round up to the nearest integer. The result is the number of points earned.
					if(item.getDescription().trim().length() % 3 == 0) {
						int earnedPoints = (int) Math.ceil(item.getPrice() * MULTIPLIER_FOR_DESCRIPTION_LENGTH);
						points += earnedPoints;
					}
				}
			}
		}

		// Rule 6: 6 points if the day in the purchase date is odd.
		if(receipt.getPurchaseDate().getDayOfMonth() % 2 != 0) {
			points += POINTS_FOR_ODD_PURCHASE_DAY;
		}

		LocalTime t = receipt.getPurchaseTime();
		// Rule 7: 10 points if the time of purchase is after 2:00pm and before 4:00pm.
		if (t.isAfter(LocalTime.of(14, 0)) && t.isBefore(LocalTime.of(16, 0))) {
			points += POINTS_FOR_PURCHASE_TIME;
		}

		return points;
	}

	private String generateReceiptId() {
		return UUID.randomUUID().toString();
	}

	public Map<String, Receipt> getReceiptStore() {
		return receiptStore;
	}
}
