//
//  CCTextField+BGTextField.h
//  TheFate
//
//  Created by Killua Liu on 11/4/14.
//  Copyright (c) 2014 Syzygy. All rights reserved.
//

#import "CCTextField.h"

@protocol BGPlatformTextFieldDelegate <CCPlatformTextFieldDelegate>

@optional
- (void) platformTextFieldDidBeginEditing:(CCPlatformTextField *) platformTextField;
- (void) platformTextFieldDidEndEditing:(CCPlatformTextField *) platformTextField;

@end

@interface CCTextField (BGTextField) <BGPlatformTextFieldDelegate>

@property (nonatomic, weak) id <BGPlatformTextFieldDelegate> delegate;

@end
