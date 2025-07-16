<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>
<%@ include file="/includes/dashboard-sessionguard.jsp" %>

<div class="transaction-container">
    <h2 class="form-header">FUND OPERATION</h2>

    <form id="transferForm" class="transfer-form">
        <!-- Row 1: Transaction Type -->
        <div class="form-row">
            <div class="form-group">
                <label for="txnType">Transaction Type</label>
                <select id="txnType" name="txnType" required>
                    <option value="3">Transfer - Within Bank</option>
                    <option value="4">Transfer - Outside Bank</option>
                </select>
            </div>
        </div>

        <!-- Row 2: From Account -->
        <div class="form-row">
            <div class="form-group" id="fromAccountGroup">
                <label for="fromAccount">From Account No</label>
                <input type="text" id="fromAccount" name="accountNo" required maxlength="11" inputmode="numeric"
                       pattern="\d{11}" title="Account number must be exactly 11 digits" placeholder="Enter Account No" />
            </div>
            <div class="form-group" id="peerGroupInside">
                <label for="peerAccNoInside">To Account No (Inside Bank)</label>
                <input type="text" id="peerAccNoInside" name="peerAccNoInside" maxlength="11" inputmode="numeric"
                       pattern="\d{11}" title="Account number must be exactly 11 digits" placeholder="Enter Receiver's Account No" />
            </div>
        </div>

        <!-- Row 3: To Account No (Inside Bank only) -->


        <!-- Row 4: Outside Bank Fields -->
        <div id="extraInfoFields" style="display: none;">
            <div class="form-row" id="peerGroupOutside">
                <div class="form-group">
                    <label for="peerAccNoOutside">To Account No (Outside Bank)</label>
                    <input type="text" id="peerAccNoOutside" name="peerAccNoOutside" maxlength="15" inputmode="numeric"
                           pattern="[1-9]\d{4,14}" title="Account number must be 5 to 15 digits" placeholder="Enter Receiver's Account No" />
                </div>
                <div class="form-group">
                    <label for="peerName">Receiver Name</label>
                    <input type="text" id="peerName" name="peerName" name="name" maxlength="70"
							pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*" placeholder="Eg: Rahul Sharma"
							oninput="this.value = this.value.replace(/[0-9]/g, '')"
							title="Name should contain only letters, spaces, hyphens, and apostrophes (1–70 characters)." />
                </div>
            </div>

            <div class="form-row">
                <div class="form-group">
                    <label for="peerBankName">Receiver Bank Name</label>
                    <input type="text" id="peerBankName" name="peerBankName" maxlength="70"
							pattern="[A-Za-z]+(?:[\-' ][A-Za-z]+)*" placeholder="Eg: HDFC Bank" 
							oninput="this.value = this.value.replace(/[0-9]/g, '')"
							title="Bank Name should contain only letters, spaces, hyphens, and apostrophes (1–70 characters)."/>
                </div>
                <div class="form-group">
                    <label for="peerIFSCCode">Receiver IFSC Code</label>
                    <input type="text" id="peerIFSCCode" name="peerIFSCCode" maxlength="11" pattern="^[A-Z]{4}0[A-Z0-9]{6}$"
                    	oninput="this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g, '')"
       					title="Enter a valid IFSC code (e.g., HDFC0001234)" placeholder="Eg: HDFC0001234" />
                </div>
            </div>
        </div>

        <!-- Row 5: Amount & Password -->
        <div class="form-row">
            <div class="form-group">
                <label for="amount">Amount</label>
               <input type="text" id="amount" name="amount" required  maxlength="6" title="Amount must be between ₹1 and ₹1,00,000 (one lakh), with up to 2 decimal places"
       				placeholder="₹1–₹1,00,000 (max 2 decimal places)" />

            </div>
            <div class="form-group password-toggle-group">
                <label for="txnPassword">Transaction Password</label>
                <div class="password-wrapper">
                    <input type="password" id="txnPassword" name="txnPassword" maxlength="20"
							pattern="(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[\W_]).{8,20}" required  placeholder="Enter your password" 
							title="Password must be 8-20 characters, include uppercase, lowercase, number, and a special character."/>
                    <span id="togglePassword" class="material-icons toggle-password" title="Show Password">visibility</span>
                </div>
            </div>
        </div>

        <!-- Row 6: Description -->
        <div class="form-row">
            <div class="form-group full-width">
                <label for="description">Description (Optional)</label>
                <input type="text" id="description" name="description" maxlength="50" placeholder="Eg: Rent, Deposit" />
            </div>
        </div>

        <!-- Row 7: Submit -->
        <div class="form-row">
            <div class="form-group full-width">
                <button type="submit" class="btn-submit">Submit</button>
            </div>
        </div>
    </form>
</div>
