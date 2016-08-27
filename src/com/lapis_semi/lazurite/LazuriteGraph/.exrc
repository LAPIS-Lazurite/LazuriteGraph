if &cp | set nocp | endif
let s:cpo_save=&cpo
set cpo&vim
nmap gx <Plug>NetrwBrowseX
nnoremap <silent> <Plug>NetrwBrowseX :call netrw#NetrwBrowseX(expand("<cfile>"),0)
nnoremap <SNR>46_: :=v:count ? v:count : ''
let &cpo=s:cpo_save
unlet s:cpo_save
set autoindent
set background=dark
set backspace=indent,eol,start
set fileencodings=ucs-bom,utf-8,default,latin1
set helplang=jp
set hidden
set history=2000
set ignorecase
set incsearch
set listchars=tab:>-,trail:-,nbsp:%,extends:>,precedes:<,eol:<
set nomodeline
set mouse=a
set printoptions=paper:a4
set ruler
set runtimepath=~/.vim/bundle/neobundle.vim/,~/.vim,/var/lib/vim/addons,~/.vim/bundle/nerdtree/,~/.vim/bundle/grep.vim/,~/.vim/bundle/vim-fugitive/,~/.vim/bundle/vim-ruby/,~/.vim/bundle/.neobundle,/usr/share/vim/vimfiles,/usr/share/vim/vim74,/usr/share/vim/vimfiles/after,/var/lib/vim/addons/after,~/.vim/after
set shiftwidth=4
set showmatch
set smartcase
set smartindent
set suffixes=.bak,~,.swp,.o,.info,.aux,.log,.dvi,.bbl,.blg,.brf,.cb,.ind,.idx,.ilg,.inx,.out,.toc,.class
set tabstop=4
set whichwrap=h,l
set nowrapscan
" vim: set ft=vim :
