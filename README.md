# CelloSplit

CelloSplit is an offline-first Android app for expense splitting and personal finance tracking with UPI-ready settlement flows.

## Current implementation status

This first implementation milestone includes:
- Jetpack Compose app scaffold with gradient finance-themed shell.
- Bottom navigation tabs: Home, Transactions, Analytics, Account.
- Center FAB for global add actions placeholder.
- Settlement domain engine with equal/custom split and pre-payment support.
- INR formatting utility and UPI payment intent builder.
- Unit test for the key `₹3270 / 6 members / Ayaan prepaid ₹200` scenario.

## Next milestone

- Room data layer and repositories.
- ViewModels and screen state models.
- Create Group and Add Expense flows.
- Group summary screen with settlement actions.
- UPI callback parser and paid status updates.
