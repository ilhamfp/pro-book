<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>Detail</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta name="google-signin-client_id" content="1039450104464-p0bpievqv6nfcbhrcvbl2vrdkg7jgnnk.apps.googleusercontent.com">
    <link rel="stylesheet" type="text/css" media="screen" href="/public/css/main.css" />
</head>
<body>
    <?php require_once('navbar.php') ?>
    <div class="container">
        <section id="book-detail">
            <div id=title-desc>
                <h1 class="page-header"><?php echo $data['book']['title']?></h1>
                <p id="author"><?php echo $data['book']['author']?></p>
                <p id="synopsys"><?php echo $data['book']['synopsis']?></p>
            </div>
            <div id="img-rating">
                <img id="img-detail" src="<?php echo $data['book']['bookPicture']?>">
                <div id="rating-star">
                    <?php
                        $i = 1;
                        while ($i <= $data['book']['avg_rating']) { ?>
                            <img class="star" src="/public/images/star.png" alt="">
                        <?php
                            $i++;
                        }

                        for ($j = $i; $j <= 5; $j++) { ?>
                            <img class="star" src="/public/images/star-unfilled.png" alt="">
                        <?php } ?>
                </div>
                <p><?php echo sprintf('%0.1f', $data['book']['avg_rating'])?>/5.0</p>
                <p><?php 
                    if ($data['book']['price'] != -1) {
                        echo "Rp. " . sprintf('%0.2f', $data['book']['price']);
                    }
                    else {
                        echo "Not available";
                    }
                ?></p>
            </div>
        </section>
        <section id="order">
            <div id="order-qty">
                <p class="detail-sub-header">Order</p>
                <label>Jumlah :</label>
                <select id="total-order">
                    <?php for ($i = 1; $i <= 10; $i++) { ?>
                    <option value="<?php echo $i?>"><?php echo $i ?></option>
                    <?php } ?>
                </select>
            </div>
                <label for="token" class="inp">
                  <input type="text" id="token" placeholder="&nbsp;">
                  <span class="label">Token</span>
                  <span class="border"></span>
                </label>
            <button onclick="order('<?php echo $data['book']['bookID']; ?>')" <?php if ($data['book']['price'] == -1) {echo "disabled";}?>>Order</button>
        </section>
        <section id="reviews">
            <p class="detail-sub-header">Reviews</p>
            <?php foreach ($data['review'] as $review) { ?>

            <div class="cust-review">
                <img src="<?php echo $review['userPicture']?>" class="review-thumbnail">
                <div class="review-detail">
                    <div>
                        <p class="reviewer">@<?php echo $review['username']?></p>
                        <p class="review-statement"><?php echo $review['comment']?></p>
                    </div>
                    <div class="review-rating">
                        <img src="/public/images/star.png" alt="">
                        <p><?php echo sprintf('%0.1f', $review['rating'])?>/5.0</p>
                    </div>
                </div>
            </div>

            <?php } ?>
        </section>
        <section id="recommendations">
            <p class="detail-sub-header">Recommendations</p>
            <?php if (is_array($data['recommendation']) || is_object($data['recommendation'])){
                foreach ($data['recommendation'] as $element) { ?>
            <div class="result-element">
                <div class="result-wrapper">
                    <img src="<?php echo $element['bookPicture']?>" class="thumbnail">
                    <div class="result-description">
                        <p class="result-title"><?php echo $element['title']?></p>
                        <?php if(isset($element['author'])) { ?>
                            <p class="result-info"><?php echo $element['author']?></p>
                        <?php } ?>
                        <p class="result-desc"><?php echo $element['synopsis']?></p>
                        <button class="button-recommendation" onclick="window.location.href = '/detail/index/<?php echo $element['bookID']?>'" <?php if ($element['bookID'] == -1) {echo 'disabled';}?>>Detail</button>
                    </div>
                </div>
            </div>
            <?php } } ?>
        </section>
    </div>
    <div id="notif">
        <div id="dialog-msg">
            <img id="check-notif" src="/public/images/check.png">
            <div id="msg-detail">
                <p id="text-msg-detail">Pemesanan Berhasil!</p>
                <p>Nomor Transaksi : <span id="transaction-number">3<span></p>
            </div>
            <img id="close-notif" onclick="close_notif()" src="/public/images/close.png">
        </div>
    </div>
    <script src="/public/js/detail.js"></script>
    <script>
        function onLoad() {
            gapi.load('auth2', function() {
            gapi.auth2.init();
            });
        }        
        function signOut() {
            var auth2 = gapi.auth2.getAuthInstance();
            auth2.signOut().then(function () {
                console.log('User signed out.');
            });
        }
    </script>
</body>
</html>
