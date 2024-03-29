const response = require('../controllers/res');
const nasabah = require('../models/nasabah');
const transaksi = require('../models/transaksi');
var speakeasy = require('speakeasy');

exports.transfer = function(req, res) {
	const pengirim = req.body.no_pengirim;
	const penerima = req.body.no_penerima;
	const jumlah = Number(req.body.jumlah);
	const token = req.body.token;
	console.log(token)
	console.log(token[0])
	console.log(token[1])

	nasabah.getKeyByCard(pengirim, function(error, rows) {
	    if (error) {
	       console.log(error)
	        return res.status(500).json({
	            message : 'Internal server error'
	        }); 
	    }
	    else {
	        if (rows.length == 0) {
	            console.log("TIDAK VALID: "+pengirim);
	            response.notFound('No kartu pengirim tidak ditemukan', res);
	        } else {
	            console.log("VALIDASI TOKEN: " + token)
            	if (speakeasy.totp.verify({	secret: rows[0].secret_key,
                                            encoding: 'base32',
                                            token: token })) {

        			nasabah.getNasabahByCard(pengirim, function(error, rows) {
        				if (error) {
        					console.log(error);
        					return res.status(500).json({
        		                message : 'Internal server error'
        		            });
        				}
        				else {
        					if (rows.length == 0) {
                                console.log("Tidak ditemukan nomor kartu");
        						response.notFound('No kartu pengirim tidak ditemukan', res);
        					}
        					else if (Number(rows[0].saldo) < jumlah) {
                                console.log("Saldo tidak cukup");
        						response.notAcceptable('Saldo pengirim tidak cukup', res);
        					}
        					else {
        						let saldo_pengirim = Number(rows[0].saldo);
        						nasabah.getNasabahByCard(penerima, function(error, rows) {
        							if (error) {
        								console.log(error);
        								return res.status(500).json({
        							        message : 'Internal server error'
        							    });
        							}
        							else {
        								if (rows.length == 0) {
                                            console.log("Tidak ditemukan nomor kartu");
        									response.notFound('No kartu penerima tidak ditemukan', res);
        								}
        								else {
        									let saldo_penerima = Number(rows[0].saldo);
        									console.log(saldo_penerima + jumlah);
        									nasabah.updateBalanceByCard(penerima, pengirim, (saldo_penerima + jumlah), (saldo_pengirim - jumlah), function(error) {
        										if (error) {
        											console.log(error);
        											return res.status(500).json({
        								                message : 'Internal server error'
        								            });
        										}
        										else {
        											transaksi.insertNewTransaction(penerima, pengirim, jumlah, function(error) {
        												if (error) {
        													console.log(error);
        													return res.status(500).json({
        										                message : error
        										            });
        												}
        												else {
                                                            console.log("Transfer berhasil");
        													response.ok('Transfer berhasil', res);
        												}
        											})
        										}
        									});
        								}
        							}
        						})
        					}
        				}
        			});

            	} else {
                    console.log("Token salah");
            		response.notAcceptable('Token salah', res);
            	}
	        }
	    }
	});
	
}
