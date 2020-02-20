\NeedsTeXFormat{LaTeX2e} % Latex version
\ProvidesClass{homework}[2020/02/14 My class] % Name of my class

%\LoadClass[a4paper, 12pt]{report} % Load
\LoadClass{article} % Load class file

% Load packages only once

% Encoding
\RequirePackage[T1]{fontenc} % Output
\RequirePackage[utf8]{inputenc} % Input

\RequirePackage[icelandic, english]{babel} % Internationalization
\RequirePackage[a4paper, margin=1in]{geometry} % Margin, padding, etc. 

% Standard

\RequirePackage{graphicx} % Everything graphics related, \resizebox, \includegraphics
\RequirePackage{fancyhdr} % Fancy headers
\RequirePackage{subfiles} % Split tex files, \subfile

% Symbols, highlighting and more
\RequirePackage{empheq} % Backbone asmsmath, math equation
\RequirePackage[cache=false]{minted} % Highlight code
\RequirePackage{caption} % Options to modify captions
\RequirePackage{gensymb} % Typeset units of measurement, \degree, \celsius

% Bibliography
\RequirePackage{bookmark} % Skip rerun with .out not
\RequirePackage{biblatex}
\RequirePackage{csquotes}

% Document navigation
\RequirePackage[nottoc]{tocbibind} % Add index to table of contents
\RequirePackage{imakeidx} % Index page
\RequirePackage{hyperref}
\RequirePackage{chngcntr} % Counter

% Utility
\RequirePackage{enumitem} % Enumerate with \alph*, \roman*
\RequirePackage{multirow} % Multiple rows in table
\RequirePackage[table]{xcolor} % Colour related, color cells

% Tikz stuff todo
%\usepackage{tikz}
%\usepackage{tkz-graph}
%\usepackage{pgf}
%\usepackage{tikz-qtree}
%\usetikzlibrary{arrows.meta,arrows, positioning, chains, fit, shapes, calc, trees}

%=====================================================================
% Setup
%=====================================================================

\hypersetup{
    colorlinks=true, % Set colour links, not the box
    linkcolor=blue,  % Link colour
}

\counterwithin{figure}{section} % Reset Figure counter after each section, 1.1, 1.2, 2.1

%=====================================================================
% Commands
%=====================================================================

% For drawing FSM
%---------------------------------------------------------------------

% Defining a double circle
\newcommand{\setnode}[2] {
% Double circle
 \tikzset{
  old inner xsep/.estore in=\oldinnerxsep,
  old inner ysep/.estore in=\oldinnerysep,
  double circle/.style 2 args={
   circle,
   old inner xsep=\pgfkeysvalueof{/pgf/inner xsep},
   old inner ysep=\pgfkeysvalueof{/pgf/inner ysep},
   /pgf/inner xsep=\oldinnerxsep+#1,
   /pgf/inner ysep=\oldinnerysep+#1,
   alias=sourcenode,
   append after command={
    let     \p1 = (sourcenode.center),
    \p2 = (sourcenode.east),
    \n1 = {\x2-\x1-#1-0.5*\pgflinewidth}
    in
    node [inner sep=0pt, draw, circle, minimum width=2.35*\n1,at=(\p1),#2] {}
   }
  },
  double circle/.default={6pt}{}
 }
% Normal circle
 \tikzset{
  old inner xsep/.estore in=\oldinnerxsep,
  old inner ysep/.estore in=\oldinnerysep,
  normal circle/.style 2 args={
   circle,
   old inner xsep=\pgfkeysvalueof{/pgf/inner xsep},
   old inner ysep=\pgfkeysvalueof{/pgf/inner ysep},
   /pgf/inner xsep=\oldinnerxsep+#1,
   /pgf/inner ysep=\oldinnerysep+#1,
  },
  normal circle/.default={6pt}{}
 }
 % Invisable node for Start
 \tikzset{novertex/.style = {shape=circle,draw opacity = 0,minimum size=1.5em}}
}

%--------------------------NODES---------------------------------------

% Draw a invisable node, #1 = node, #2 = node name, #3 = x-position, #4 = y-position
\newcommand{\nonode}[4] {
  \node[novertex] at (#3,#4) (#1) {#2};
}

% Draw a double circle, #1 = node, #2 = node name, #3 = x-position, #4 = y-position
\newcommand{\doublenode}[4] {
 \node[double circle, draw] at (#3,#4) (#1) {#2};
}

% Draw a normal circle, #1 = node, #2 = node name, #3 = x-position, #4 = y-position
\newcommand{\normalnode}[4] {
  \node[normal circle, draw] at (#3,#4) (#1) {#2};
}

%--------------------------EDGES---------------------------------------

% Draw a edge for starting node, #1 = from node, $2 = to node, #3 = above/below, #4 = color of weight
\newcommand{\startedge}[4] {
  \draw[-{Latex[scale=1.5]}] (#1) -- (#2) node [midway, #3, color=#4] {Start};
}

% Draw a straight line with a weight, #1 = from node, #2 = to node, #3 = above/below, ,#4 = weight #5 = color of weight
\newcommand{\edgeline}[5] {
  \draw[-{Latex[scale=1.5]}] (#1) -- (#2) node [midway, #3, color=#5] {#4};
}

% Draw a curved line with a weight, #1 = from node, #2 = to node, #3 = above/below, ,#4 = weight #5 = color of weight, #6 bend right/left = num
\newcommand{\edgecurve}[6] {
  \draw[-{Latex[scale=1.5]}] (#1) to[#6] node [midway, #3, color=#5] {#4} (#2);
}

% Draw a curved right-line with a weight, #1 = from node, #2 = to node, #3 = above/below, ,#4 = weight #5 = color of weight
\newcommand{\edgecurver}[5] {
  \draw[-{Latex[scale=1.5]}] (#1) to[bend right=25] node [midway, #3, color=#5] {#4} (#2);
}

% Draw a curved left-line with a weight, #1 = from node, #2 = to node, #3 = above/below, ,#4 = weight #5 = color of weight
\newcommand{\edgecurvel}[5] {
  \draw[-{Latex[scale=1.5]}] (#1) to[bend left=25] node [midway, #3, color=#5] {#4} (#2);
}

% Draw a looped arrow, #1 = node, #2 = direction, 3# = weight, 4# = color of weight
% Default is #1, north, NULL, blue
\newcommand{\edgeloop}[4] {
 \draw[-{Latex[scale=1.5]}] (#1.#2) -- (#1.#2) arc (-90:250:0.4cm)  node [midway, xshift=-0.75mm, above, color=#4] {#3};
}

% Question and answer setup
%---------------------------------------------------------------------

% Question
\newenvironment{question}[1] %
{
  \subsection*{#1}
}%
{
\rule{\textwidth}{0.8pt}
\ignorespacesafterend
}

% Answer
\newenvironment{answer} %
{
\begin{flushleft}
}%
{
\end{flushleft}
\rule{\textwidth}{0.8pt}
\rule[5mm]{\textwidth}{0.8pt}
\ignorespacesafterend
}

% Title page
%---------------------------------------------------------------------

% Make a titlepage, #1 = class, #2 = homework number, #3 = Author, #4 = Author, #5 = Teacher
\newcommand{\frontpage}[5] {
\begin{titlepage}

\newcommand{\HRule}{\rule{\linewidth}{0.5mm}}

\center

\textsc{\LARGE Háskóli Íslands}\\[1.5cm]
\textsc{\Large #1}\\[0.5cm]

\HRule \\[0.4cm]
{ \huge \bfseries #2}\\[0.4cm] % Heiti skjals
\HRule \\[1.5cm]

\begin{minipage}[t]{0.4\textwidth}
\begin{flushleft} \large

% Authors
\ifthenelse{\equal{#4}{}}
{\textbf{Höfundur:}\\ Hjalti Geir Garðarsson}
{\textbf{Höfundar:}\\ Hjalti Geir Garðarsson\\#3\\#4}

\end{flushleft}
\end{minipage}
~
\begin{minipage}[t]{0.4\textwidth}
\begin{flushright} \large
\textbf{Kennari:} \\
#5
\end{flushright}
\end{minipage}\\[2cm]


{\large \today}\\[2cm]


\includegraphics{pallas.jpg}\\[1cm]


\vfill

\end{titlepage}
}
