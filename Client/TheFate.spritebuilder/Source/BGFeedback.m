//
//  BGFeedback.m
//  TheFate
//
//  Created by Killua Liu on 4/18/14.
//  Copyright 2014 Apportable. All rights reserved.
//

#import "BGFeedback.h"
#import "BGZoneScene.h"

@implementation BGFeedback {
    BOOL _keyboardIsShown;
    float _keyboardHeight;
#if defined(APPORTABLE)
    BOOL _textFieldIsEditing;
#endif
}

- (void)didLoadFromCCB
{
    [super didLoadFromCCB];
    [self registerForKeyboardNotifications];
    
    _titleText = [[UITextField alloc] initWithFrame:CGRectZero];
    _titleText.alpha = 0.9f;
    _titleText.borderStyle = UITextBorderStyleRoundedRect;
    _titleText.contentVerticalAlignment = UIControlContentVerticalAlignmentCenter;
    _titleText.font = [UIFont systemFontOfSize:[self fontSize]];
    _titleText.backgroundColor = [UIColor lightGrayColor];
    _titleText.placeholder = TIP_FEEDBACK_TITLE;
    [_titleText setValue:[UIColor grayColor] forKeyPath:@"_placeholderLabel.textColor"];
    _titleText.delegate = self;
    [[CCDirector sharedDirector].view addSubview:_titleText];
    
    _contentText = [[UITextView alloc] initWithFrame:CGRectZero];
    _contentText.alpha = 0.9f;
    _contentText.font = [UIFont systemFontOfSize:[self fontSize]];
    _contentText.backgroundColor = [UIColor lightGrayColor];
    _contentText.delegate = self;
    [[CCDirector sharedDirector].view addSubview:_contentText];
    
    [self setUIViewFrame];
    [self setAllUIViewsHidden:YES];
    
    _okayButton.enabled = NO;
}

- (CGFloat)fontSize
{
    return (IS_PHONE) ? 10 : 20;
}

- (void)setUIViewFrame
{
//  Title text
    CGPoint glPos = ccp(SCREEN_CENTER.x-self.bgSize.width*0.44f, SCREEN_CENTER.y+self.bgSize.height*0.3f);
    CGPoint origin = [[CCDirector sharedDirector] convertToUI:glPos];
    
    CGSize size = self.bgSize;
    size.width *= SCALE_MULTIPLIER * 0.88f;
    size.height *= SCALE_MULTIPLIER * 0.1f;
    
    CGRect frame = CGRectZero;
    frame.origin = origin;
    frame.size = size;
    _titleText.frame = frame;
    
//  Content text
    glPos = ccp(SCREEN_CENTER.x-self.bgSize.width*0.44f, SCREEN_CENTER.y+self.bgSize.height*0.17f);
    origin = [[CCDirector sharedDirector] convertToUI:glPos];
    
    size = self.bgSize;
    size.width *= SCALE_MULTIPLIER * 0.88f;
    size.height *= SCALE_MULTIPLIER * 0.4f;
    
    frame.origin = origin;
    frame.size = size;
    _contentText.frame = frame;
}

- (void)removeFromParent
{
    [super removeFromParent];
    [_titleText removeFromSuperview];
    [_contentText removeFromSuperview];
}

- (void)okay:(CCButton *)sender
{
    [[BGClient sharedClient] sendFeedbackRequestWithTitle:_titleText.text content:_contentText.text];
    [super okay:sender];
}

- (void)dismiss
{
    [self unregisterForKeyboardNotifications];
    [super dismiss];
}

#pragma mark - TextField delegate
- (void)textFieldDidBeginEditing:(CCTextField *)textField
{
#if defined(APPORTABLE)
    _textFieldIsEditing = YES;
#endif
    
    if(_keyboardIsShown) [self focusOnTextField];
}

- (void)textFieldDidEndEditing:(CCTextField *)textField
{
#if defined(APPORTABLE)
    _textFieldIsEditing = NO;
#endif
    
    [self endFocusingOnTextField];
    _okayButton.enabled = (_titleText.text.length > 0);
}

- (BOOL)textFieldShouldReturn:(CCTextField *)textField
{
    if ([textField isEqual:_titleText]) {
        [_contentText becomeFirstResponder];
    }
    return YES;
}

#pragma mark - TextView delegate
- (void)textViewDidBeginEditing:(UITextView *)textView
{
#if defined(APPORTABLE)
    _textFieldIsEditing = YES;
#endif
    
    if(_keyboardIsShown) [self focusOnTextField];
    
    _contactLabel.visible = NO;
}

- (void)textViewDidEndEditing:(UITextView *)textView
{
#if defined(APPORTABLE)
    _textFieldIsEditing = NO;
#endif
    
    [self endFocusingOnTextField];
}


#pragma mark Focusing on Text Field
#ifdef __CC_PLATFORM_IOS
- (void)focusOnTextField
{
#if defined(APPORTABLE)
    // Ensure that all textfields have actually been positioned before checkings textField.frame property,
    // it's possible for the apportable keyboard notification to be fired before the mainloop has had a chance to kick of a scheduler update
    CCDirector *director = [CCDirector sharedDirector];
    [director.scheduler update:0.0];
#endif
    
    CGSize windowSize = [[CCDirector sharedDirector] viewSize];
    
    // Find the location of the textField
    float fieldCenterY = _contentText.frame.origin.y - (_contentText.frame.size.height/2);
    
    // Upper third part of the screen
    float upperThirdHeight = windowSize.height / 3;
    
    if (fieldCenterY > upperThirdHeight)
    {
        // Slide the main view up
        
        // Calculate offset
        float dstYLocation = windowSize.height / 4;
        float offset = -(fieldCenterY - dstYLocation);
        
//      Changed by Killua
        CCNode *runningNode = [CCDirector sharedDirector].runningScene.children.lastObject;
        CCNode *node = [runningNode getChildByName:@"okay" recursively:YES];
        if (!node) node = [runningNode getChildByName:@"login" recursively:YES];
        if (node) {
            CGPoint glPos = [node convertToWorldSpace:CGPointZero];
            offset = -(_keyboardHeight - glPos.y*SCALE_MULTIPLIER);
        } else {
            if (offset < -_keyboardHeight) offset = -_keyboardHeight;
        }
        
#if defined(APPORTABLE)
        // Apportable does not support changing the openglview position, so we will just change the current scenes position instead
        CCScene *runningScene = [[CCDirector sharedDirector] runningScene];
        CGPoint newPosition = runningScene.position;
        newPosition.y = (offset * -1);
        runningScene.position = newPosition;
#else
        // Calcualte target frame
        UIView* view = [[CCDirector sharedDirector] view];
        CGRect frame = view.frame;
        frame.origin.y = offset;
        
        // Do animation
        [UIView beginAnimations: @"textFieldAnim" context: nil];
        [UIView setAnimationBeginsFromCurrentState: YES];
        [UIView setAnimationDuration: 0.2f];
        
        view.frame = frame;
        [UIView commitAnimations];
#endif
    }
}

- (void) endFocusingOnTextField
{
    // Slide the main view back down
    
#if defined(APPORTABLE)
    // Apportable does not support changing the openglview position, so we will just change the current scenes position instead
    CCScene *runningScene = [[CCDirector sharedDirector] runningScene];
    CGPoint newPosition = CGPointZero;
    newPosition.y = 0.0f;
    runningScene.position = newPosition;
#else
    UIView* view = [[CCDirector sharedDirector] view];
    [UIView beginAnimations: @"textFieldAnim" context: nil];
    [UIView setAnimationBeginsFromCurrentState: YES];
    [UIView setAnimationDuration: 0.2f];
    
    CGRect frame = view.frame;
    frame.origin = CGPointZero;
    view.frame = frame;
    
    [UIView commitAnimations];
#endif
}
#endif


#pragma mark Keyboard Notifications
- (void)registerForKeyboardNotifications
{
#ifdef __CC_PLATFORM_IOS
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWasShown:)
                                                 name:UIKeyboardDidShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self
                                             selector:@selector(keyboardWillBeHidden:)
                                                 name:UIKeyboardWillHideNotification object:nil];
#endif
}

- (void) unregisterForKeyboardNotifications
{
#ifdef __CC_PLATFORM_IOS
    [[NSNotificationCenter defaultCenter] removeObserver:self];
#endif
}

#ifdef __CC_PLATFORM_IOS
- (void)keyboardWasShown:(NSNotification*)notification
{
    _keyboardIsShown = YES;
    
    UIView* view = [[CCDirector sharedDirector] view];
    
    NSDictionary* info = [notification userInfo];
    NSValue* value = [info objectForKey:UIKeyboardFrameEndUserInfoKey];
    CGRect frame = [value CGRectValue];
    frame = [view.window convertRect:frame toView:view];
    
    CGSize kbSize = frame.size;
    
    _keyboardHeight = kbSize.height;
    
    BOOL focusOnTextField = (_titleText.isEditing || _contentText.isFirstResponder);
    
#if defined(APPORTABLE)
    focusOnTextField = _textFieldIsEditing;
#endif
    
    if (focusOnTextField)
    {
        [self focusOnTextField];
    }
}

- (void) keyboardWillBeHidden:(NSNotification*) notification
{
    _keyboardIsShown = NO;
    
    if (_contentText.text.length == 0) _contactLabel.visible = YES;
}
#endif

@end
