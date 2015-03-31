//
//  BGStoreKitHelper.h
//  TheFate
//
//  Created by Killua Liu on 12/3/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <StoreKit/StoreKit.h>

@interface BGStoreKitHelper : NSObject <SKProductsRequestDelegate, SKPaymentTransactionObserver>

+ (id)sharedStoreKitHelper;

- (void)requestProductDataWithProductId:(NSString *)productId;
- (void)removeTransactionObserver;

@end
