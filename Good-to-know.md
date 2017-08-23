## Nice to know :

### SP and DP in xml layouts : 
They both are automatically scaled to be the same approximate physical size regardless of the density of the pixels on the screen.
The first Android phone was 160dpi and on those 1dp = 1px
Modern devices have 480dpi or more
Conversion : px = dp * (dpi / 160)
SP works like DP but are scale to the user preferences, it's important to use them for accessibility.