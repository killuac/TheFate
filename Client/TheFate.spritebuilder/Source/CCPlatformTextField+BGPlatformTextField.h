//
//  CCPlatformTextField+BGPlatformTextField.h
//  TheFate
//
//  Created by Killua Liu on 11/15/14.
//  Copyright (c) 2014 Apportable. All rights reserved.
//

#import "CCPlatformTextField.h"

typedef NS_ENUM(NSInteger, BGTextAutocapitalizationType) {
    BGTextAutocapitalizationTypeNone,
    BGTextAutocapitalizationTypeWords,
    BGTextAutocapitalizationTypeSentences,
    BGTextAutocapitalizationTypeAllCharacters,
};

typedef NS_ENUM(NSInteger, BGTextAutocorrectionType) {
    BGTextAutocorrectionTypeDefault,
    BGTextAutocorrectionTypeNo,
    BGTextAutocorrectionTypeYes,
};

typedef NS_ENUM(NSInteger, BGReturnKeyType) {
    BGReturnKeyDefault,
    BGReturnKeyGo,
    BGReturnKeyGoogle,
    BGReturnKeyJoin,
    BGReturnKeyNext,
    BGReturnKeyRoute,
    BGReturnKeySearch,
    BGReturnKeySend,
    BGReturnKeyYahoo,
    BGReturnKeyDone,
    BGReturnKeyEmergencyCall,
};

typedef NS_ENUM(NSInteger, BGKeyboardType) {
    BGKeyboardTypeDefault,                // Default type for the current input method.
    BGKeyboardTypeASCIICapable,           // Displays a keyboard which can enter ASCII characters, non-ASCII keyboards remain active
    BGKeyboardTypeNumbersAndPunctuation,  // Numbers and assorted punctuation.
    BGKeyboardTypeURL,                    // A type optimized for URL entry (shows . / .com prominently).
    BGKeyboardTypeNumberPad,              // A number pad (0-9). Suitable for PIN entry.
    BGKeyboardTypePhonePad,               // A phone pad (1-9, *, 0, #, with letters under the numbers).
    BGKeyboardTypeNamePhonePad,           // A type optimized for entering a person's name or phone number.
    BGKeyboardTypeEmailAddress,           // A type optimized for multiple email address entry (shows space @ . prominently).
    BGKeyboardTypeDecimalPad NS_ENUM_AVAILABLE_IOS(4_1),   // A number pad with a decimal point.
    BGKeyboardTypeTwitter NS_ENUM_AVAILABLE_IOS(5_0),      // A type optimized for twitter text entry (easy access to @ #)
    BGKeyboardTypeWebSearch NS_ENUM_AVAILABLE_IOS(7_0),    // A default keyboard type with URL-oriented addition (shows space . prominently).
};

@interface CCPlatformTextField (BGPlatformTextField)

@property (nonatomic) BOOL secureTextEntry;
@property (nonatomic, copy) NSString *placeholder;
@property (nonatomic, strong) CCColor *textColor;

@property (nonatomic) BGTextAutocapitalizationType autocapitalizationType;
@property (nonatomic) BGTextAutocorrectionType autocorrectionType;
@property (nonatomic) BGReturnKeyType returnKeyType;
@property (nonatomic) BGKeyboardType keyboardType;

- (void)becomeFirstResponder;

@end
