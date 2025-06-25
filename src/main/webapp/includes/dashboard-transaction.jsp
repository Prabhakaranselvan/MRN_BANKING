<div class="transaction-container">
    <h2 class="form-header">FUND OPERATION</h2>

    <form id="transferForm" class="transfer-form">
        <!-- Row 1: Transaction Type -->
        <div class="form-row">
            <div class="form-group">
                <label for="txnType">Transaction Type</label>
                <select id="txnType" name="txnType" required>
                    <option value="3">Transfer</option>
                </select>
            </div>
        </div>

        <!-- Row 2: From Account & To Account (conditional) -->
        <div class="form-row">
            <div class="form-group" id="fromAccountGroup">
                <label for="fromAccount">From Account No</label>
                <input type="text" id="fromAccount" name="accountNo" required maxlength="11" inputmode="numeric" 
                pattern="\d{11}"  title="Account number must be exactly 11 digits" placeholder="Enter Account No" />
            </div>

            <div class="form-group peer-group" id="peerGroup">
                <label for="peerAccNo">To Account No</label>
                <input type="text" id="peerAccNo" name="peerAccNo" maxlength="15" inputmode="numeric"  
               pattern="[1-9]\d{4,14}"  title="Account number must be 5 to 15 digits" placeholder="Enter Receiver's Account No" />
            </div>
        </div>

        <!-- Row 3: Amount & Password -->
        <div class="form-row">
            <div class="form-group">
                <label for="amount">Amount</label>
                <input type="text" id="amount" name="amount" required placeholder=" ₹1–100K (max 2 decimals) | Eg: 500.00" />
            </div>
            <div class="form-group password-toggle-group">
                <label for="txnPassword">Transaction Password</label>
                <div class="password-wrapper">
                    <input type="password" id="txnPassword" name="txnPassword" required placeholder="Enter your password" />
                    <span id="togglePassword" class="material-icons toggle-password">visibility_off</span>
                </div>
            </div>
        </div>

        <!-- Row 4: Description -->
        <div class="form-row">
            <div class="form-group full-width">
                <label for="description">Description</label>
                <input type="text" id="description" name="description" maxlength="50" placeholder="Eg: Rent, Deposit" />
            </div>
        </div>

        <!-- Row 5: Submit -->
        <div class="form-row">
            <div class="form-group full-width">
                <button type="submit" class="btn-submit">Submit</button>
            </div>
        </div>
    </form>
</div>
