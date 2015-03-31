//
//  BGStoreKitHelper.m
//  TheFate
//
//  Created by Killua Liu on 12/3/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "BGStoreKitHelper.h"
#import "BGClient.h"
#import "BGUtil.h"

@implementation BGStoreKitHelper

static id instanceOfStoreKitHelper;
+ (id)sharedStoreKitHelper
{
    if (!instanceOfStoreKitHelper) {
        instanceOfStoreKitHelper = [[self alloc] init];
    }
    return instanceOfStoreKitHelper;
}

- (id)init
{
    if (self = [super init]) {
        [[SKPaymentQueue defaultQueue] addTransactionObserver:self];
    }
    return self;
}

- (void)requestProductDataWithProductId:(NSString *)productId
{
    if ([SKPaymentQueue canMakePayments]) {
        [[CCDirector sharedDirector].runningScene.children.lastObject addLoadingMaskNode:NO];
        
        NSSet *set = [NSSet setWithObject:productId];
        SKProductsRequest *request = [[SKProductsRequest alloc] initWithProductIdentifiers:set];
        request.delegate = self;
        [request start];
    }
}

#pragma mark - StoreKit Delegate
- (void)productsRequest:(SKProductsRequest *)request didReceiveResponse:(SKProductsResponse *)response
{
    SKPayment *payment = [SKPayment paymentWithProduct:response.products.lastObject];
    [[SKPaymentQueue defaultQueue] addPayment:payment];
}

- (void)paymentQueue:(SKPaymentQueue *)queue updatedTransactions:(NSArray *)transactions
{
    for (SKPaymentTransaction *transaction in transactions) {
        switch (transaction.transactionState) {
            case SKPaymentTransactionStatePurchased:
                [self purchasedTransaction:transaction];
                break;
                
            case SKPaymentTransactionStateFailed:
                [self failedTransaction:transaction];
                break;
                
            default:
                break;
        }
    }
}

- (void)purchasedTransaction:(SKPaymentTransaction *)transaction
{
    if (transaction.transactionIdentifier.length > 0) {
        NSString *receiptStr = [transaction.transactionReceipt base64Encoding];
        [[BGClient sharedClient] sendBuyGoldCoinRequestWithReceipt:receiptStr];
    }
    [[SKPaymentQueue defaultQueue] finishTransaction:transaction];
}

- (void)failedTransaction:(SKPaymentTransaction *)transaction
{
    [[CCDirector sharedDirector].runningScene.children.lastObject removeLoadingMaskNode];
    [[SKPaymentQueue defaultQueue] finishTransaction:transaction];
    
    if (SKErrorPaymentCancelled != transaction.error.code) {
        [BGUtil showInformationWithMessage:INFO_STORE_CONNECTION_FAILED];
    }
}

- (void)removeTransactionObserver
{
    [[SKPaymentQueue defaultQueue] removeTransactionObserver:self];
}

@end
